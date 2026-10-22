package com.essenza.draco.modules.devolution.application.services.flow;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.OrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.application.output.repository.MotiveDevolutionRepository;
import com.essenza.draco.modules.devolution.application.output.repository.OrderDevolutionRepository;
import com.essenza.draco.modules.devolution.application.output.repository.RefundMethodRepository;
import com.essenza.draco.modules.devolution.application.output.repository.ReturnCaseRepository;
import com.essenza.draco.modules.devolution.application.output.repository.ReturnMethodRepository;
import com.essenza.draco.modules.devolution.domain.model.DevolutionRuleViolation;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;
import com.essenza.draco.modules.devolution.domain.model.ReturnItem;
import com.essenza.draco.shared.common.sales.OrderReturns;
import com.essenza.draco.shared.common.sales.OrderReturns.ReturnableLine;
import com.essenza.draco.shared.common.sales.OrderReturns.ReturnableOrder;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Piezas comunes del flujo de devoluciones: orden, catálogos, cantidades devolvibles y DTO. */
@Component
class ReturnSoporte {

    static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    /** Estados de la orden que admiten devoluciones. */
    static final Set<String> RETURNABLE_STATES = Set.of("PAID", "PROCESSING", "READY", "SHIPPED", "DELIVERED");

    private final ReturnCaseRepository cases;
    private final OrderDevolutionRepository devolutions;
    private final OrderReturns orders;
    private final MotiveDevolutionRepository motives;
    private final ReturnMethodRepository returnMethods;
    private final RefundMethodRepository refundMethods;

    ReturnSoporte(ReturnCaseRepository cases, OrderDevolutionRepository devolutions, OrderReturns orders,
                  MotiveDevolutionRepository motives, ReturnMethodRepository returnMethods,
                  RefundMethodRepository refundMethods) {
        this.cases = cases;
        this.devolutions = devolutions;
        this.orders = orders;
        this.motives = motives;
        this.returnMethods = returnMethods;
        this.refundMethods = refundMethods;
    }

    static LocalDateTime now() {
        return LocalDateTime.now(BOGOTA);
    }

    ReturnCase locked(Long id) {
        return cases.lock(id).orElseThrow(() -> new NotFoundException("No se encontró la devolución " + id + "."));
    }

    ReturnableOrder order(Long orderId) {
        return orders.find(orderId).orElseThrow(() -> new NotFoundException("No se encontró la orden " + orderId + "."));
    }

    /** La orden debe estar pagada (o más adelante) para devolver productos. */
    ReturnableOrder returnableOrder(Long orderId) {
        ReturnableOrder order = order(orderId);
        if (!RETURNABLE_STATES.contains(order.state())) {
            throw new ConflictException("Solo se devuelven productos de órdenes pagadas, en proceso, enviadas o entregadas "
                    + "(la orden " + orderId + " está " + order.state() + ").");
        }
        return order;
    }

    void checkCatalogs(Long motiveId, Long returnMethodId, Long refundMethodId) {
        if (motiveId != null && motives.findById(motiveId).isEmpty()) {
            throw new NotFoundException("No se encontró el motivo de devolución " + motiveId + ".");
        }
        if (returnMethodId != null) {
            var method = returnMethods.findById(returnMethodId)
                    .orElseThrow(() -> new NotFoundException("No se encontró la forma de devolución " + returnMethodId + "."));
            if (Boolean.FALSE.equals(method.getActive())) {
                throw new DevolutionRuleViolation("La forma de devolución \"" + method.getName() + "\" está inactiva.");
            }
        }
        if (refundMethodId != null) {
            var method = refundMethods.findById(refundMethodId)
                    .orElseThrow(() -> new NotFoundException("No se encontró la forma de reembolso " + refundMethodId + "."));
            if (Boolean.FALSE.equals(method.getActive())) {
                throw new DevolutionRuleViolation("La forma de reembolso \"" + method.getName() + "\" está inactiva.");
            }
        }
    }

    /**
     * Nueva línea de la devolución con SKU, nombre y precio congelados de la orden; valida que
     * la línea sea de la orden y que la cantidad quepa en lo que falta por devolver.
     */
    ReturnItem item(ReturnableOrder order, Long productOrderId, int quantity, String observation,
                    Map<Long, Integer> alreadyReturned) {
        ReturnableLine line = line(order, productOrderId);
        checkQuantity(line, quantity, alreadyReturned);
        return ReturnItem.requested(line.lineId(), line.productId(), line.skuId(), line.skuCode(), line.productName(),
                quantity, line.unitPrice(), observation == null || observation.isBlank() ? null : observation.trim());
    }

    ReturnableLine line(ReturnableOrder order, Long productOrderId) {
        return order.lines().stream().filter(l -> Objects.equals(l.lineId(), productOrderId)).findFirst()
                .orElseThrow(() -> new DevolutionRuleViolation("La línea " + productOrderId + " no pertenece a la orden "
                        + order.id() + "."));
    }

    static void checkQuantity(ReturnableLine line, int quantity, Map<Long, Integer> alreadyReturned) {
        int returned = alreadyReturned.getOrDefault(line.lineId(), 0);
        int available = Math.max(0, line.quantity() - returned);
        if (quantity <= 0) {
            throw new DevolutionRuleViolation("La cantidad a devolver debe ser mayor que cero.");
        }
        if (quantity > available) {
            throw new DevolutionRuleViolation("\"" + (line.productName() == null ? "Línea " + line.lineId() : line.productName())
                    + "\": se vendieron " + line.quantity() + ", ya se devolvieron " + returned + "; se pueden devolver "
                    + available + ".");
        }
    }

    Map<Long, Integer> returnedUnits(Long orderId, Long excludeDevolutionId) {
        return cases.returnedUnits(orderId, excludeDevolutionId);
    }

    ReturnCase save(ReturnCase returnCase) {
        return cases.save(returnCase);
    }

    /** DTO con auditoría, acciones disponibles y líneas. */
    OrderDevolutionDto toDto(ReturnCase c) {
        OrderDevolutionDto dto = devolutions.findById(c.id()).orElseGet(OrderDevolutionDto::new);
        dto.setActions(c.actions());
        dto.setDetails(c.items().stream().map(item -> detailDto(c, item)).toList());
        return dto;
    }

    static OrderDevolutionDetailDto detailDto(ReturnCase c, ReturnItem item) {
        return OrderDevolutionDetailDto.builder()
                .id(item.id())
                .orderDevolutionId(c.id())
                .productOrderId(item.productOrderId())
                .productId(item.productId())
                .skuId(item.skuId())
                .skuCode(item.skuCode())
                .productName(item.productName())
                .quantity(item.quantity())
                .receivedQuantity(item.receivedQuantity())
                .unitPrice(item.unitPrice())
                .condition(item.condition() == null ? null : item.condition().name())
                .restockingFee(item.restockingFee())
                .refundAmount(item.refundAmount())
                .restock(item.restock())
                .restockedQuantity(item.restockedQuantity())
                .observation(item.observation())
                .build();
    }
}
