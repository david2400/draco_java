package com.essenza.draco.modules.devolution.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Devolución de una orden con su flujo: solicitar → aprobar → recibir → inspeccionar (reingreso
 * al stock y valor acreditado) → reembolsar; se puede rechazar antes de inspeccionar.
 * Java puro: las transiciones y cálculos se validan aquí.
 */
public final class ReturnCase {

    /** Total y cantidad de una línea de la orden (para el valor proporcional). */
    public record LineValue(BigDecimal total, int quantity) {
    }

    /** Resultado de la inspección de una línea. */
    public record Inspection(ItemCondition condition, Boolean restock, BigDecimal restockingFee) {
    }

    private final Long id;
    private final Long orderId;
    private DevolutionState state;
    private Long motiveId;
    private String observation;
    private Long returnMethodId;
    private Long refundMethodId;
    private String externalReference;
    private Long warehouseId;
    private BigDecimal creditValue;
    private BigDecimal refundedAmount;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String receivedBy;
    private LocalDateTime receivedAt;
    private String inspectedBy;
    private LocalDateTime inspectedAt;
    private String inspectionNotes;
    private String rejectedReason;
    private String rejectedBy;
    private LocalDateTime rejectedAt;
    private String refundedBy;
    private LocalDateTime refundedAt;
    private String refundDetail;
    private final List<ReturnItem> items;

    public ReturnCase(Long id, Long orderId, DevolutionState state, Long motiveId, String observation, Long returnMethodId,
                      Long refundMethodId, String externalReference, Long warehouseId, BigDecimal creditValue,
                      BigDecimal refundedAmount, String approvedBy, LocalDateTime approvedAt, String receivedBy,
                      LocalDateTime receivedAt, String inspectedBy, LocalDateTime inspectedAt, String inspectionNotes,
                      String rejectedReason, String rejectedBy, LocalDateTime rejectedAt, String refundedBy,
                      LocalDateTime refundedAt, String refundDetail, List<ReturnItem> items) {
        this.id = id;
        this.orderId = Objects.requireNonNull(orderId, "orderId");
        this.state = state == null ? DevolutionState.PENDING : state;
        this.motiveId = motiveId;
        this.observation = observation;
        this.returnMethodId = returnMethodId;
        this.refundMethodId = refundMethodId;
        this.externalReference = externalReference;
        this.warehouseId = warehouseId;
        this.creditValue = creditValue;
        this.refundedAmount = refundedAmount == null ? BigDecimal.ZERO.setScale(2) : refundedAmount;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.receivedBy = receivedBy;
        this.receivedAt = receivedAt;
        this.inspectedBy = inspectedBy;
        this.inspectedAt = inspectedAt;
        this.inspectionNotes = inspectionNotes;
        this.rejectedReason = rejectedReason;
        this.rejectedBy = rejectedBy;
        this.rejectedAt = rejectedAt;
        this.refundedBy = refundedBy;
        this.refundedAt = refundedAt;
        this.refundDetail = refundDetail;
        this.items = new ArrayList<>(items == null ? List.of() : items);
    }

    /** Nueva solicitud (pendiente) con sus líneas. */
    public static ReturnCase request(Long orderId, Long motiveId, String observation, Long returnMethodId,
                                     Long refundMethodId, String externalReference, List<ReturnItem> items) {
        if (motiveId == null) {
            throw new DevolutionRuleViolation("Indica el motivo de la devolución.");
        }
        if (observation == null || observation.isBlank()) {
            throw new DevolutionRuleViolation("Describe la devolución (observación).");
        }
        if (items == null || items.isEmpty()) {
            throw new DevolutionRuleViolation("Agrega al menos un producto a devolver.");
        }
        long distinct = items.stream().map(ReturnItem::productOrderId).distinct().count();
        if (distinct != items.size()) {
            throw new DevolutionRuleViolation("Cada línea de la orden debe aparecer una sola vez en la devolución.");
        }
        return new ReturnCase(null, orderId, DevolutionState.PENDING, motiveId, observation.trim(), returnMethodId,
                refundMethodId, blankToNull(externalReference), null, null, BigDecimal.ZERO.setScale(2), null, null,
                null, null, null, null, null, null, null, null, null, null, null, items);
    }

    /** Agrega una línea (solo pendiente; una por línea de la orden). */
    public void addItem(ReturnItem item) {
        requireLinesEditable();
        if (items.stream().anyMatch(existing -> Objects.equals(existing.productOrderId(), item.productOrderId()))) {
            throw new DevolutionRuleViolation("La línea " + item.productOrderId()
                    + " ya está en la devolución: cambia su cantidad en lugar de agregarla otra vez.");
        }
        items.add(item);
    }

    /** Cambia cantidad y observación de una línea (solo pendiente). */
    public ReturnItem changeItem(Long itemId, int quantity, String observation) {
        requireLinesEditable();
        ReturnItem current = item(itemId);
        ReturnItem changed = new ReturnItem(current.id(), current.productOrderId(), current.productId(), current.skuId(),
                current.skuCode(), current.productName(), quantity, current.unitPrice(),
                observation == null ? current.observation() : observation, null, null, null, null, null, 0);
        items.set(items.indexOf(current), changed);
        return changed;
    }

    /** Valida que una línea se pueda quitar (solo pendiente y si no es la última). */
    public void checkRemovable(Long itemId) {
        requireLinesEditable();
        item(itemId);
    }

    /** Datos generales editables (pendiente o aprobada). */
    public void edit(Long motiveId, String observation, Long returnMethodId, Long refundMethodId, String externalReference) {
        if (!state.headerEditable()) {
            throw new DevolutionRuleViolation("La devolución está " + describe(state)
                    + ": ya no se pueden cambiar sus datos generales.");
        }
        if (motiveId != null) {
            this.motiveId = motiveId;
        }
        if (observation != null && !observation.isBlank()) {
            this.observation = cut(observation.trim(), 255);
        }
        if (returnMethodId != null) {
            this.returnMethodId = returnMethodId;
        }
        if (refundMethodId != null) {
            this.refundMethodId = refundMethodId;
        }
        if (externalReference != null) {
            this.externalReference = blankToNull(externalReference);
        }
    }

    private void requireLinesEditable() {
        if (!state.linesEditable()) {
            throw new DevolutionRuleViolation("Los productos solo se cambian mientras la devolución está pendiente (está "
                    + describe(state) + ").");
        }
    }

    public void approve(String by, Long returnMethodId, LocalDateTime now) {
        require(DevolutionState.PENDING, "aprobar");
        if (items.isEmpty()) {
            throw new DevolutionRuleViolation("La devolución no tiene productos: agrégalos antes de aprobarla.");
        }
        if (returnMethodId != null) {
            this.returnMethodId = returnMethodId;
        }
        this.approvedBy = actor(by);
        this.approvedAt = now;
        this.state = DevolutionState.APPROVED;
    }

    public void reject(String by, String reason, LocalDateTime now) {
        if (!state.canReject()) {
            throw new DevolutionRuleViolation("No se puede rechazar una devolución " + describe(state)
                    + " (solo pendiente, aprobada o recibida).");
        }
        if (reason == null || reason.isBlank()) {
            throw new DevolutionRuleViolation("Indica el motivo del rechazo.");
        }
        this.rejectedReason = cut(reason.trim(), 255);
        this.rejectedBy = actor(by);
        this.rejectedAt = now;
        this.state = DevolutionState.REJECTED;
    }

    /** Recepción: unidades recibidas por línea (las que no se indican se reciben completas). */
    public void receive(String by, Map<Long, Integer> receivedByItem, String notes, LocalDateTime now) {
        require(DevolutionState.APPROVED, "recibir");
        for (Long itemId : receivedByItem.keySet()) {
            item(itemId);
        }
        for (ReturnItem item : items) {
            Integer units = receivedByItem.get(item.id());
            item.receive(units == null ? item.quantity() : units);
        }
        if (items.stream().mapToInt(ReturnItem::received).sum() == 0) {
            throw new DevolutionRuleViolation("No se recibió ninguna unidad: si el cliente no entregó nada, rechaza la devolución.");
        }
        this.receivedBy = actor(by);
        this.receivedAt = now;
        appendNotes(notes);
        this.state = DevolutionState.RECEIVED;
    }

    /**
     * Inspección: estado, reingreso y cargo por línea; calcula el valor a acreditar. Devuelve las
     * líneas cuyas unidades vuelven al stock (las que lleguen con unidades recibidas).
     */
    public List<ReturnItem> inspect(String by, Long warehouseId, Map<Long, Inspection> byItem,
                                    Map<Long, LineValue> orderLines, String notes, LocalDateTime now) {
        require(DevolutionState.RECEIVED, "inspeccionar");
        for (Long itemId : byItem.keySet()) {
            item(itemId);
        }
        for (ReturnItem item : items) {
            Inspection inspection = byItem.get(item.id());
            if (inspection == null && item.received() > 0) {
                throw new DevolutionRuleViolation("Falta la inspección de \"" + item.label() + "\".");
            }
            LineValue line = orderLines.get(item.productOrderId());
            if (line == null) {
                throw new DevolutionRuleViolation("La línea " + item.productOrderId() + " ya no existe en la orden.");
            }
            ItemCondition condition = inspection == null ? ItemCondition.NEW : inspection.condition();
            item.inspect(condition, inspection == null ? Boolean.FALSE : inspection.restock(),
                    inspection == null ? null : inspection.restockingFee(), line.total(), line.quantity());
        }
        this.warehouseId = warehouseId;
        this.creditValue = items.stream().map(ReturnItem::refundAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        this.inspectedBy = actor(by);
        this.inspectedAt = now;
        appendNotes(notes);
        this.state = DevolutionState.INSPECTED;
        List<ReturnItem> restock = items.stream()
                .filter(item -> Boolean.TRUE.equals(item.restock()) && item.received() > 0).toList();
        restock.forEach(ReturnItem::markRestocked);
        return restock;
    }

    /** Monto a reembolsar en dinero: por defecto el valor acreditado; puede ser menor (saldo a favor). */
    public BigDecimal refundAmountFor(BigDecimal requested) {
        require(DevolutionState.INSPECTED, "reembolsar");
        BigDecimal credit = creditValue == null ? BigDecimal.ZERO.setScale(2) : creditValue;
        if (requested == null) {
            return credit;
        }
        BigDecimal amount = requested.setScale(2, RoundingMode.HALF_UP);
        if (amount.signum() < 0) {
            throw new DevolutionRuleViolation("El monto a reembolsar no puede ser negativo.");
        }
        if (amount.compareTo(credit) > 0) {
            throw new DevolutionRuleViolation("El reembolso (" + amount.toPlainString() + ") supera el valor acreditado ("
                    + credit.toPlainString() + ").");
        }
        return amount;
    }

    public void markRefunded(String by, Long refundMethodId, BigDecimal amount, String detail, LocalDateTime now) {
        require(DevolutionState.INSPECTED, "reembolsar");
        if (refundMethodId != null) {
            this.refundMethodId = refundMethodId;
        }
        this.refundedAmount = amount.setScale(2, RoundingMode.HALF_UP);
        this.refundedBy = actor(by);
        this.refundedAt = now;
        this.refundDetail = cut(detail, 500);
        this.state = DevolutionState.REFUNDED;
    }

    /** Unidades que siguen contando como devueltas de cada línea de la orden. */
    public int unitsFor(ReturnItem item) {
        if (state == DevolutionState.REJECTED) {
            return 0;
        }
        return item.receivedQuantity() == null ? item.quantity() : item.receivedQuantity();
    }

    public List<String> actions() {
        return state.actions();
    }

    private ReturnItem item(Long itemId) {
        return items.stream().filter(item -> Objects.equals(item.id(), itemId)).findFirst()
                .orElseThrow(() -> new DevolutionRuleViolation("La línea " + itemId + " no pertenece a la devolución."));
    }

    private void require(DevolutionState expected, String action) {
        if (state != expected) {
            throw new DevolutionRuleViolation("No se puede " + action + " una devolución " + describe(state)
                    + " (debe estar " + describe(expected) + ").");
        }
    }

    private void appendNotes(String notes) {
        if (notes == null || notes.isBlank()) {
            return;
        }
        String value = inspectionNotes == null || inspectionNotes.isBlank() ? notes.trim() : inspectionNotes + " · " + notes.trim();
        this.inspectionNotes = cut(value, 255);
    }

    static String describe(DevolutionState state) {
        return switch (state) {
            case PENDING -> "pendiente";
            case APPROVED -> "aprobada";
            case RECEIVED -> "recibida";
            case INSPECTED -> "inspeccionada";
            case REFUNDED -> "reembolsada";
            case REJECTED -> "rechazada";
        };
    }

    private static String actor(String by) {
        return by == null || by.isBlank() ? null : cut(by.trim(), 100);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String cut(String value, int max) {
        return value == null || value.length() <= max ? value : value.substring(0, max);
    }

    public Long id() { return id; }
    public Long orderId() { return orderId; }
    public DevolutionState state() { return state; }
    public Long motiveId() { return motiveId; }
    public String observation() { return observation; }
    public Long returnMethodId() { return returnMethodId; }
    public Long refundMethodId() { return refundMethodId; }
    public String externalReference() { return externalReference; }
    public Long warehouseId() { return warehouseId; }
    public BigDecimal creditValue() { return creditValue; }
    public BigDecimal refundedAmount() { return refundedAmount; }
    public String approvedBy() { return approvedBy; }
    public LocalDateTime approvedAt() { return approvedAt; }
    public String receivedBy() { return receivedBy; }
    public LocalDateTime receivedAt() { return receivedAt; }
    public String inspectedBy() { return inspectedBy; }
    public LocalDateTime inspectedAt() { return inspectedAt; }
    public String inspectionNotes() { return inspectionNotes; }
    public String rejectedReason() { return rejectedReason; }
    public String rejectedBy() { return rejectedBy; }
    public LocalDateTime rejectedAt() { return rejectedAt; }
    public String refundedBy() { return refundedBy; }
    public LocalDateTime refundedAt() { return refundedAt; }
    public String refundDetail() { return refundDetail; }
    public List<ReturnItem> items() { return List.copyOf(items); }
}
