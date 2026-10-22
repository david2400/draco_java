package com.essenza.draco.modules.sales.domain.model.order;

import java.util.List;

import com.essenza.draco.modules.sales.domain.model.OrderStatus;

/** Reglas de preparación, despacho y entrega. */
public final class Fulfillment {

    private Fulfillment() {
    }

    /** Línea a preparar: unidades pedidas y preparadas. */
    public record PickLine(Long lineId, String label, int quantity, int picked) {
        public boolean complete() {
            return picked >= quantity;
        }
    }

    /** Se prepara mientras la orden está pagada, en preparación o lista. */
    public static void checkCanPick(OrderStatus status) {
        if (status != OrderStatus.PAID && status != OrderStatus.PROCESSING && status != OrderStatus.READY) {
            throw new OrderRuleViolation("Solo se preparan órdenes pagadas o en preparación (la orden está " + status + ").");
        }
    }

    public static int checkPicked(PickLine line, Integer picked) {
        if (picked == null || picked < 0) {
            throw new OrderRuleViolation("Las unidades preparadas de " + line.label() + " no pueden ser negativas.");
        }
        if (picked > line.quantity()) {
            throw new OrderRuleViolation("De " + line.label() + " se pidieron " + line.quantity()
                    + " unidades: no se pueden preparar " + picked + ".");
        }
        return picked;
    }

    /** Todas las líneas preparadas (una orden sin líneas no está lista). */
    public static void requireAllPicked(List<PickLine> lines) {
        if (lines.isEmpty()) {
            throw new OrderRuleViolation("La orden no tiene productos para despachar.");
        }
        List<String> missing = lines.stream().filter(line -> !line.complete())
                .map(line -> line.label() + " (" + line.picked() + "/" + line.quantity() + ")").toList();
        if (!missing.isEmpty()) {
            throw new OrderRuleViolation("Falta preparar: " + String.join(", ", missing) + ".");
        }
    }

    /** Despachar: solo envíos, con la orden pagada/en preparación/lista y guía si hay transportadora. */
    public static void checkCanShip(OrderStatus status, DeliveryInfo delivery, Long carrierId, String trackingNumber) {
        if (!status.canTransitionTo(OrderStatus.SHIPPED)) {
            throw new OrderRuleViolation("No se puede despachar una orden " + status + ".");
        }
        if (delivery.isPickup()) {
            throw new OrderRuleViolation("La orden se retira en tienda: márcala como lista y luego como entregada.");
        }
        delivery.requireShippingAddress();
        if (carrierId != null && (trackingNumber == null || trackingNumber.isBlank())) {
            throw new OrderRuleViolation("Indica el número de guía de la transportadora.");
        }
    }

    public static void checkCanDeliver(OrderStatus status, DeliveryInfo delivery) {
        if (!status.canTransitionTo(OrderStatus.DELIVERED)) {
            throw new OrderRuleViolation(delivery.isPickup()
                    ? "Marca la orden como lista para retirar antes de entregarla."
                    : "Despacha la orden antes de marcarla como entregada.");
        }
        if (status == OrderStatus.READY && !delivery.isPickup()) {
            throw new OrderRuleViolation("Los envíos se despachan antes de marcarse como entregados.");
        }
    }
}
