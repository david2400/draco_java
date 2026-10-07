package com.essenza.draco.modules.product_details.domain.model.unit;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Unidad de medida. {@code factor} expresa cuántas unidades base de su magnitud
 * equivale una unidad (1 kg = 1000 g → g.factor = 0.001 si la base es kg).
 * La unidad base de cada magnitud tiene factor 1.
 */
public final class UnitOfMeasure {

    private static final Pattern CODE = Pattern.compile("^[a-z0-9_]{1,20}$");
    private static final MathContext PRECISION = new MathContext(20, RoundingMode.HALF_UP);
    /** Decimales con los que se guarda el factor (DECIMAL(24,12)). */
    public static final int FACTOR_SCALE = 12;

    private final Long id;
    private final String code;
    private final String symbol;
    private final String name;
    private final UnitDimension dimension;
    private final BigDecimal factor;
    private final boolean base;
    private final int decimals;
    private final boolean active;

    public UnitOfMeasure(Long id, String code, String symbol, String name, UnitDimension dimension,
                         BigDecimal factor, boolean base, int decimals, boolean active) {
        this.id = id;
        this.code = normalizeCode(code);
        this.name = requireText(name, "El nombre es obligatorio.", 255);
        this.symbol = symbol == null || symbol.isBlank() ? this.code : requireText(symbol, "", 20);
        this.dimension = Objects.requireNonNull(dimension, "dimension");
        if (factor == null || factor.signum() <= 0) {
            throw new UnitRuleViolation("El factor de conversión debe ser mayor que 0.");
        }
        this.factor = base ? BigDecimal.ONE : plain(factor.setScale(FACTOR_SCALE, RoundingMode.HALF_UP));
        if (this.factor.signum() <= 0) {
            throw new UnitRuleViolation("El factor de conversión es demasiado pequeño.");
        }
        if (decimals < 0 || decimals > 6) {
            throw new UnitRuleViolation("Los decimales deben estar entre 0 y 6.");
        }
        if (base && !active) {
            throw new UnitRuleViolation("La unidad base de una magnitud no se puede desactivar.");
        }
        this.base = base;
        this.decimals = decimals;
        this.active = active;
    }

    /** Código en minúsculas: letras, números y "_" (máx. 20). */
    public static String normalizeCode(String code) {
        String value = code == null ? "" : code.trim().toLowerCase(Locale.ROOT);
        if (!CODE.matcher(value).matches()) {
            throw new UnitRuleViolation("El código solo admite minúsculas, números y _ (1 a 20 caracteres).");
        }
        return value;
    }

    private static String requireText(String value, String message, int max) {
        if (value == null || value.isBlank()) {
            throw new UnitRuleViolation(message);
        }
        String trimmed = value.trim();
        if (trimmed.length() > max) {
            throw new UnitRuleViolation("Máximo " + max + " caracteres.");
        }
        return trimmed;
    }

    /** Convierte {@code value} de esta unidad a {@code target} (misma magnitud). */
    public BigDecimal convert(BigDecimal value, UnitOfMeasure target) {
        Objects.requireNonNull(value, "value");
        if (dimension != target.dimension || dimension == UnitDimension.OTHER && !Objects.equals(id, target.id)) {
            throw new UnitRuleViolation("No se puede convertir de " + symbol + " (" + dimension + ") a "
                    + target.symbol + " (" + target.dimension + ").");
        }
        if (Objects.equals(code, target.code)) {
            return value;
        }
        return plain(value.multiply(factor).divide(target.factor, PRECISION));
    }

    /** Sin ceros sobrantes y sin notación científica (2500, no 2.5E+3). */
    public static BigDecimal plain(BigDecimal value) {
        BigDecimal stripped = value.stripTrailingZeros();
        return stripped.scale() < 0 ? stripped.setScale(0) : stripped;
    }

    /** Copia con nuevo factor (al cambiar la unidad base de la magnitud). */
    public UnitOfMeasure rescaled(BigDecimal newFactor, boolean newBase) {
        return new UnitOfMeasure(id, code, symbol, name, dimension, newFactor, newBase, decimals, active || newBase);
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public UnitDimension getDimension() { return dimension; }
    public BigDecimal getFactor() { return factor; }
    public boolean isBase() { return base; }
    public int getDecimals() { return decimals; }
    public boolean isActive() { return active; }
}
