package com.essenza.draco.modules.billing.domain.model;

import java.time.LocalDate;

/**
 * Numeración autorizada (resolución DIAN): prefijo, rango y vigencia. {@link #take(LocalDate)}
 * entrega el siguiente consecutivo y avanza; falla si está inactiva, vencida o agotada.
 */
public final class NumberingResolution {

    private final Long id;
    private final DocumentKind kind;
    private final String prefix;
    private final String resolutionNumber;
    private final LocalDate resolutionDate;
    private final String technicalKey;
    private final long fromNumber;
    private final long toNumber;
    private long nextNumber;
    private final LocalDate validFrom;
    private final LocalDate validTo;
    private final BillingEnvironment environment;
    private final boolean active;

    public NumberingResolution(Long id, DocumentKind kind, String prefix, String resolutionNumber, LocalDate resolutionDate,
                               String technicalKey, long fromNumber, long toNumber, long nextNumber, LocalDate validFrom,
                               LocalDate validTo, BillingEnvironment environment, boolean active) {
        if (kind == null) {
            throw new BillingRuleViolation("Indica si la numeración es de facturas o de notas crédito.");
        }
        String cleanPrefix = prefix == null ? "" : prefix.trim().toUpperCase(java.util.Locale.ROOT);
        if (!cleanPrefix.matches("[A-Z0-9]{0,4}")) {
            throw new BillingRuleViolation("El prefijo tiene hasta 4 letras o números.");
        }
        if (fromNumber < 0 || toNumber < fromNumber) {
            throw new BillingRuleViolation("El rango no es válido: el número final debe ser mayor o igual al inicial.");
        }
        if (nextNumber < fromNumber || nextNumber > toNumber + 1) {
            throw new BillingRuleViolation("El siguiente número debe estar dentro del rango autorizado.");
        }
        if (validFrom != null && validTo != null && validTo.isBefore(validFrom)) {
            throw new BillingRuleViolation("La vigencia termina antes de empezar.");
        }
        if (kind == DocumentKind.INVOICE && environment == BillingEnvironment.PRODUCTION
                && (resolutionNumber == null || resolutionNumber.isBlank() || technicalKey == null || technicalKey.isBlank()
                || validTo == null)) {
            throw new BillingRuleViolation("En producción la numeración de facturas exige número de resolución, clave técnica y vigencia.");
        }
        this.id = id;
        this.kind = kind;
        this.prefix = cleanPrefix;
        this.resolutionNumber = resolutionNumber == null || resolutionNumber.isBlank() ? null : resolutionNumber.trim();
        this.resolutionDate = resolutionDate;
        this.technicalKey = technicalKey == null || technicalKey.isBlank() ? null : technicalKey.trim();
        this.fromNumber = fromNumber;
        this.toNumber = toNumber;
        this.nextNumber = nextNumber;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.environment = environment == null ? BillingEnvironment.TEST : environment;
        this.active = active;
    }

    /** Siguiente consecutivo; avanza la numeración. */
    public long take(LocalDate today) {
        if (!active) {
            throw new BillingRuleViolation("La numeración " + label() + " está inactiva.");
        }
        if (validFrom != null && today.isBefore(validFrom)) {
            throw new BillingRuleViolation("La numeración " + label() + " rige desde " + validFrom + ".");
        }
        if (validTo != null && today.isAfter(validTo)) {
            throw new BillingRuleViolation("La numeración " + label() + " venció el " + validTo
                    + ": registra una nueva resolución.");
        }
        if (nextNumber > toNumber) {
            throw new BillingRuleViolation("La numeración " + label() + " está agotada (hasta " + toNumber
                    + "): registra una nueva resolución.");
        }
        return nextNumber++;
    }

    public long remaining() {
        return Math.max(0, toNumber - nextNumber + 1);
    }

    /** Ya se usó al menos un número (no se cambian prefijo ni rango inicial). */
    public boolean used() {
        return nextNumber > fromNumber;
    }

    public String label() {
        return (prefix.isEmpty() ? "" : prefix + " ") + fromNumber + "–" + toNumber;
    }

    public Long id() { return id; }
    public DocumentKind kind() { return kind; }
    public String prefix() { return prefix; }
    public String resolutionNumber() { return resolutionNumber; }
    public LocalDate resolutionDate() { return resolutionDate; }
    public String technicalKey() { return technicalKey; }
    public long fromNumber() { return fromNumber; }
    public long toNumber() { return toNumber; }
    public long nextNumber() { return nextNumber; }
    public LocalDate validFrom() { return validFrom; }
    public LocalDate validTo() { return validTo; }
    public BillingEnvironment environment() { return environment; }
    public boolean active() { return active; }
}
