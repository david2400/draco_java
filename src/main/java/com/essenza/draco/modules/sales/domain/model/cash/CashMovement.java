package com.essenza.draco.modules.sales.domain.model.cash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

/** Entrada o salida manual de efectivo en un turno (con motivo). */
public record CashMovement(Long id, Long sessionId, CashMovementType type, BigDecimal amount, String reason, String reference,
                           String createdBy, Instant createdAt) {

    public CashMovement {
        if (type == null) {
            throw new CashRuleViolation("Indica si es una entrada (IN) o una salida (OUT).");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new CashRuleViolation("El valor del movimiento debe ser mayor que 0.");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        if (reason == null || reason.isBlank()) {
            throw new CashRuleViolation("Indica el motivo del movimiento.");
        }
        reason = reason.trim();
        reference = reference == null || reference.isBlank() ? null : reference.trim();
        createdBy = createdBy == null || createdBy.isBlank() ? null : createdBy.trim();
    }

    /** Efecto sobre el efectivo de la caja. */
    public BigDecimal signedAmount() {
        return type == CashMovementType.IN ? amount : amount.negate();
    }
}
