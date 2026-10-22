package com.essenza.draco.modules.sales.application.services.order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.order.OrderActionDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.PickOrderDto;
import com.essenza.draco.modules.sales.application.dto.order.ShipOrderDto;
import com.essenza.draco.modules.sales.application.input.order.OrderFulfillmentUseCase;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger.LineState;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger.OrderDetails;
import com.essenza.draco.modules.sales.application.output.repository.OrderReferences;
import com.essenza.draco.modules.sales.domain.model.OrderStatus;
import com.essenza.draco.modules.sales.domain.model.order.Fulfillment;
import com.essenza.draco.modules.sales.domain.model.order.OrderEventType;
import com.essenza.draco.modules.sales.domain.model.order.OrderRuleViolation;

/**
 * Preparación (picking por línea), lista para despacho o retiro, despacho con transportadora
 * y guía, y entrega. Preparar la primera unidad pasa la orden de PAID a PROCESSING.
 */
@Service
@Transactional
public class OrderFulfillmentService implements OrderFulfillmentUseCase {

    private final OrderLedger ledger;
    private final OrderReferences references;
    private final OrderTransitions transitions;
    private final OrderSoporte soporte;

    public OrderFulfillmentService(OrderLedger ledger, OrderReferences references, OrderTransitions transitions,
                                   OrderSoporte soporte) {
        this.ledger = ledger;
        this.references = references;
        this.transitions = transitions;
        this.soporte = soporte;
    }

    @Override
    public OrderDto pick(Long orderId, PickOrderDto input) {
        OrderStatus status = status(orderId);
        Fulfillment.checkCanPick(status);
        Map<Long, LineState> lines = new LinkedHashMap<>();
        ledger.lineStates(orderId).forEach(line -> lines.put(line.id(), line));
        List<String> changes = new ArrayList<>();
        for (PickOrderDto.Line requested : input.lines()) {
            LineState line = lines.get(requested.lineId());
            if (line == null) {
                throw new OrderRuleViolation("La línea " + requested.lineId() + " no pertenece a la orden " + orderId + ".");
            }
            int picked = Fulfillment.checkPicked(OrderTransitions.pickLine(line), requested.pickedQuantity());
            if (picked != line.picked()) {
                ledger.setPicked(line.id(), picked);
                changes.add(line.label() + " " + picked + "/" + line.quantity());
            }
        }
        afterPicking(orderId, status, changes, input.actor());
        return soporte.view(orderId);
    }

    @Override
    public OrderDto pickAll(Long orderId, OrderActionDto input) {
        OrderStatus status = status(orderId);
        Fulfillment.checkCanPick(status);
        List<String> changes = new ArrayList<>();
        for (LineState line : ledger.lineStates(orderId)) {
            if (line.picked() != line.quantity()) {
                ledger.setPicked(line.id(), line.quantity());
                changes.add(line.label() + " " + line.quantity() + "/" + line.quantity());
            }
        }
        afterPicking(orderId, status, changes, input == null ? null : input.actor());
        return soporte.view(orderId);
    }

    private void afterPicking(Long orderId, OrderStatus status, List<String> changes, String actor) {
        if (changes.isEmpty()) {
            return;
        }
        soporte.log(orderId, OrderEventType.PICKED, null, null, String.join(", ", changes), actor);
        if (status == OrderStatus.PAID) {
            transitions.apply(orderId, OrderStatus.PROCESSING, null, OrderEventType.STATUS, null, actor);
        }
    }

    @Override
    public OrderDto ready(Long orderId, OrderActionDto input) {
        OrderStatus status = status(orderId);
        if (!status.canTransitionTo(OrderStatus.READY)) {
            throw new OrderRuleViolation("Solo una orden pagada o en preparación puede quedar lista (la orden está " + status + ").");
        }
        transitions.apply(orderId, OrderStatus.READY, null, OrderEventType.STATUS, note(input), actor(input));
        return soporte.view(orderId);
    }

    @Override
    public OrderDto ship(Long orderId, ShipOrderDto input) {
        OrderStatus status = status(orderId);
        OrderDetails details = soporte.details(orderId);
        Long carrierId = input.carrierId() != null ? input.carrierId() : details.delivery().carrierId();
        String tracking = OrderSoporte.blankToNull(input.trackingNumber());
        Fulfillment.checkCanShip(status, details.delivery(), carrierId, tracking);
        if (carrierId != null && !references.carrierActive(carrierId)) {
            throw new OrderRuleViolation("La transportadora " + carrierId + " no existe o está inactiva.");
        }
        ledger.setShipment(orderId, carrierId, tracking);
        String message = String.join(" · ", java.util.stream.Stream.of(tracking, OrderSoporte.blankToNull(input.note()))
                .filter(java.util.Objects::nonNull).toList());
        transitions.apply(orderId, OrderStatus.SHIPPED, null, OrderEventType.SHIPPED, message.isEmpty() ? null : message,
                input.actor());
        return soporte.view(orderId);
    }

    @Override
    public OrderDto deliver(Long orderId, OrderActionDto input) {
        OrderStatus status = status(orderId);
        Fulfillment.checkCanDeliver(status, soporte.details(orderId).delivery());
        transitions.apply(orderId, OrderStatus.DELIVERED, null, OrderEventType.DELIVERED, note(input), actor(input));
        return soporte.view(orderId);
    }

    private OrderStatus status(Long orderId) {
        return OrderStatus.tryParse(transitions.lock(orderId).state())
                .orElseThrow(() -> new OrderRuleViolation("La orden " + orderId + " tiene un estado desconocido."));
    }

    private static String note(OrderActionDto input) {
        return input == null ? null : OrderSoporte.blankToNull(input.note());
    }

    private static String actor(OrderActionDto input) {
        return input == null ? null : input.actor();
    }
}
