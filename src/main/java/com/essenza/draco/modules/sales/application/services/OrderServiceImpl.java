package com.essenza.draco.modules.sales.application.services;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.order.ChangeOrderStatusDto;
import com.essenza.draco.modules.sales.application.dto.order.CreateOrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderReservationDto;
import com.essenza.draco.modules.sales.application.dto.order.UpdateOrderDto;
import com.essenza.draco.modules.sales.application.input.order.ChangeOrderStatusUseCase;
import com.essenza.draco.modules.sales.application.input.order.CreateOrderUseCase;
import com.essenza.draco.modules.sales.application.input.order.DeleteOrderUseCase;
import com.essenza.draco.modules.sales.application.input.order.ExpireOrderReservationsUseCase;
import com.essenza.draco.modules.sales.application.input.order.FindOrderByIdUseCase;
import com.essenza.draco.modules.sales.application.input.order.FindOrderReservationsUseCase;
import com.essenza.draco.modules.sales.application.input.order.FindOrdersUseCase;
import com.essenza.draco.modules.sales.application.input.order.UpdateOrderUseCase;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger.OrderHeader;
import com.essenza.draco.modules.sales.application.output.repository.OrderRepository;
import com.essenza.draco.modules.sales.domain.model.OrderStatus;
import com.essenza.draco.shared.common.inventory.StockReservations;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

/**
 * Órdenes con stock (Fase 5): se crean PENDING; las líneas reservan stock; al pagar
 * se descuenta; al cancelar se libera la reserva o se devuelve lo descontado.
 * Las órdenes anteriores a la Fase 5 ({@code stock_managed = false}) no tocan el stock.
 */
@Service
@Transactional
public class OrderServiceImpl implements CreateOrderUseCase,
        UpdateOrderUseCase,
        DeleteOrderUseCase,
        FindOrderByIdUseCase,
        FindOrdersUseCase,
        ChangeOrderStatusUseCase,
        ExpireOrderReservationsUseCase,
        FindOrderReservationsUseCase {

    static final String EXPIRED_REASON = "Reserva vencida: la orden no se pagó a tiempo.";

    private final OrderRepository orderRepository;
    private final OrderLedger ledger;
    private final StockReservations reservations;
    private final Duration reservationTtl;

    public OrderServiceImpl(OrderRepository orderRepository, OrderLedger ledger, StockReservations reservations,
                            @Value("${essenza.orders.reservation-ttl:PT24H}") Duration reservationTtl) {
        this.orderRepository = orderRepository;
        this.ledger = ledger;
        this.reservations = reservations;
        this.reservationTtl = reservationTtl;
    }

    @Override
    public OrderDto create(CreateOrderDto input) {
        if (input.getState() != null && !input.getState().isBlank()
                && OrderStatus.tryParse(input.getState()).orElse(null) != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Las órdenes se crean pendientes (PENDING); cambia el estado cuando tenga líneas.");
        }
        Long id = ledger.createOrder(input.getComplementaryOrder());
        return view(id);
    }

    @Override
    public OrderDto update(Long id, UpdateOrderDto input) {
        OrderHeader header = header(id);
        ledger.updateComplementary(id, input.getComplementaryOrder());
        if (input.getState() != null && !input.getState().isBlank() && !input.getState().equalsIgnoreCase(header.state())) {
            return changeStatus(id, new ChangeOrderStatusDto(input.getState(), null));
        }
        return view(id);
    }

    @Override
    public OrderDto changeStatus(Long id, ChangeOrderStatusDto input) {
        OrderHeader header = header(id);
        OrderStatus target = OrderStatus.parse(input.state());
        Optional<OrderStatus> current = OrderStatus.tryParse(header.state());
        if (current.isPresent() && current.get() == target) {
            return view(id);
        }
        if (current.isPresent() && !current.get().canTransitionTo(target)) {
            throw new ConflictException("No se puede pasar una orden de " + current.get() + " a " + target + ".");
        }
        if (header.stockManaged() && current.isPresent()) {
            applyStock(id, current.get(), target);
        }
        String reason = target == OrderStatus.CANCELLED ? blankToNull(input.reason()) : null;
        ledger.setState(id, target.name(), reason);
        return view(id);
    }

    private void applyStock(Long id, OrderStatus from, OrderStatus to) {
        if (to == OrderStatus.PAID) {
            if (ledger.lines(id).isEmpty()) {
                throw new ConflictException("La orden " + id + " no tiene líneas: agrega productos antes de marcarla como pagada.");
            }
            reservations.commitOrder(id);
        } else if (to == OrderStatus.CANCELLED) {
            if (from == OrderStatus.PENDING) {
                reservations.releaseOrder(id, false);
            } else if (from.stockCommitted()) {
                reservations.restockOrder(id);
            }
        }
    }

    @Override
    public boolean deleteById(Long id) {
        if (orderRepository.findById(id).isEmpty()) {
            throw new NotFoundException("No se encontró la orden " + id + ".");
        }
        if (orderRepository.hasDependents(id)) {
            throw new ConflictException("No se puede eliminar la orden " + id
                    + ": tiene despachos o devoluciones asociados.");
        }
        OrderHeader header = header(id);
        if (header.stockManaged()) {
            OrderStatus status = OrderStatus.tryParse(header.state()).orElse(OrderStatus.PENDING);
            if (status == OrderStatus.PENDING) {
                reservations.releaseOrder(id, false);
            } else if (status != OrderStatus.CANCELLED) {
                throw new ConflictException("Solo se pueden eliminar órdenes pendientes o canceladas: la orden " + id
                        + " está " + status + ". Cancélala primero.");
            }
        }
        return orderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> findById(Long id) {
        return orderRepository.findById(id).map(OrderServiceImpl::withNextStates);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAll() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderReservationDto> reservations(Long orderId) {
        if (orderRepository.findById(orderId).isEmpty()) {
            throw new NotFoundException("No se encontró la orden " + orderId + ".");
        }
        return reservations.reservations(orderId).stream()
                .map(line -> new OrderReservationDto(line.id(), line.lineId(), line.skuId(), line.warehouseId(),
                        line.quantity(), line.status(), line.expiresAt()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findExpired() {
        return ledger.expiredPendingOrders(Instant.now().minus(reservationTtl));
    }

    @Override
    public boolean cancelExpired(Long orderId) {
        Optional<OrderHeader> header = ledger.lockOrder(orderId);
        if (header.isEmpty() || !header.get().stockManaged()
                || OrderStatus.tryParse(header.get().state()).orElse(null) != OrderStatus.PENDING) {
            return false;
        }
        reservations.releaseOrder(orderId, true);
        ledger.setState(orderId, OrderStatus.CANCELLED.name(), EXPIRED_REASON);
        return true;
    }

    private OrderHeader header(Long id) {
        return ledger.lockOrder(id).orElseThrow(() -> new NotFoundException("No se encontró la orden " + id + "."));
    }

    private OrderDto view(Long id) {
        return orderRepository.findById(id).map(OrderServiceImpl::withNextStates)
                .orElseThrow(() -> new NotFoundException("No se encontró la orden " + id + "."));
    }

    static OrderDto withNextStates(OrderDto dto) {
        dto.setNextStates(OrderStatus.tryParse(dto.getState())
                .map(status -> status.next().stream().map(Enum::name).toList())
                .orElseGet(() -> java.util.Arrays.stream(OrderStatus.values()).map(Enum::name).toList()));
        return dto;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
