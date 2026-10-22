package com.essenza.draco.modules.sales.domain.model.cash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

/**
 * Turno de una caja: se abre con una base en efectivo y se cierra con el arqueo
 * (efectivo esperado vs contado). Cerrado, sus valores quedan congelados.
 */
public final class CashSession {

    private final Long id;
    private final Long registerId;
    private CashSessionStatus status;
    private final String openedBy;
    private final Instant openedAt;
    private final BigDecimal openingAmount;
    private final String openingNotes;
    private String closedBy;
    private Instant closedAt;
    private BigDecimal expectedCash;
    private BigDecimal countedCash;
    private BigDecimal cashDifference;
    private String closingNotes;

    public CashSession(Long id, Long registerId, CashSessionStatus status, String openedBy, Instant openedAt,
                       BigDecimal openingAmount, String openingNotes, String closedBy, Instant closedAt,
                       BigDecimal expectedCash, BigDecimal countedCash, BigDecimal cashDifference, String closingNotes) {
        this.id = id;
        this.registerId = registerId;
        this.status = status;
        this.openedBy = openedBy;
        this.openedAt = openedAt;
        this.openingAmount = openingAmount == null ? BigDecimal.ZERO.setScale(2) : openingAmount;
        this.openingNotes = openingNotes;
        this.closedBy = closedBy;
        this.closedAt = closedAt;
        this.expectedCash = expectedCash;
        this.countedCash = countedCash;
        this.cashDifference = cashDifference;
        this.closingNotes = closingNotes;
    }

    public static CashSession open(Long registerId, String openedBy, BigDecimal openingAmount, String notes, Instant now) {
        if (openedBy == null || openedBy.isBlank()) {
            throw new CashRuleViolation("Indica quién abre la caja.");
        }
        BigDecimal base = openingAmount == null ? BigDecimal.ZERO : openingAmount.setScale(2, RoundingMode.HALF_UP);
        if (base.signum() < 0) {
            throw new CashRuleViolation("La base de apertura no puede ser negativa.");
        }
        return new CashSession(null, registerId, CashSessionStatus.OPEN, openedBy.trim(), now, base, blankToNull(notes),
                null, null, null, null, null, null);
    }

    public void requireOpen() {
        if (status != CashSessionStatus.OPEN) {
            throw new CashRuleViolation("El turno " + id + " está cerrado.");
        }
    }

    /** Cierra con el arqueo de efectivo; la diferencia es contado − esperado (positiva = sobrante). */
    public void close(String by, BigDecimal expected, BigDecimal counted, String notes, Instant now) {
        requireOpen();
        if (by == null || by.isBlank()) {
            throw new CashRuleViolation("Indica quién cierra la caja.");
        }
        if (counted == null || counted.signum() < 0) {
            throw new CashRuleViolation("Indica el efectivo contado (0 o más).");
        }
        this.status = CashSessionStatus.CLOSED;
        this.closedBy = by.trim();
        this.closedAt = now;
        this.expectedCash = expected.setScale(2, RoundingMode.HALF_UP);
        this.countedCash = counted.setScale(2, RoundingMode.HALF_UP);
        this.cashDifference = this.countedCash.subtract(this.expectedCash);
        this.closingNotes = blankToNull(notes);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public Long id() { return id; }
    public Long registerId() { return registerId; }
    public CashSessionStatus status() { return status; }
    public String openedBy() { return openedBy; }
    public Instant openedAt() { return openedAt; }
    public BigDecimal openingAmount() { return openingAmount; }
    public String openingNotes() { return openingNotes; }
    public String closedBy() { return closedBy; }
    public Instant closedAt() { return closedAt; }
    public BigDecimal expectedCash() { return expectedCash; }
    public BigDecimal countedCash() { return countedCash; }
    public BigDecimal cashDifference() { return cashDifference; }
    public String closingNotes() { return closingNotes; }
}
