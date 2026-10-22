package com.essenza.draco.modules.sales.domain.model.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Condiciones de un cupón tal como las define Promociones: tipo (porcentaje, valor fijo o
 * envío gratis), tope, compra mínima, vigencia, cupo de usos y productos o categorías a
 * los que aplica o que excluye (vacío = sin restricción).
 */
public record CouponRule(Long id, String code, String kind, BigDecimal value, BigDecimal maxDiscount, BigDecimal minOrder,
                         LocalDateTime validFrom, LocalDateTime validUntil, boolean active, Integer usageLimit,
                         int usageCount, Set<Long> products, Set<Long> excludedProducts, Set<Long> categories,
                         Set<Long> excludedCategories) {

    public static final String PERCENTAGE = "PERCENTAGE";
    public static final String FIXED_AMOUNT = "FIXED_AMOUNT";
    public static final String FREE_SHIPPING = "FREE_SHIPPING";

    public CouponRule {
        products = products == null ? Set.of() : Set.copyOf(products);
        excludedProducts = excludedProducts == null ? Set.of() : Set.copyOf(excludedProducts);
        categories = categories == null ? Set.of() : Set.copyOf(categories);
        excludedCategories = excludedCategories == null ? Set.of() : Set.copyOf(excludedCategories);
    }

    /** ¿La línea de este producto entra en el cupón? */
    public boolean covers(Long productId, Long categoryId) {
        if (excludedProducts.contains(productId) || (categoryId != null && excludedCategories.contains(categoryId))) {
            return false;
        }
        if (products.isEmpty() && categories.isEmpty()) {
            return true;
        }
        return products.contains(productId) || (categoryId != null && categories.contains(categoryId));
    }

    /**
     * Valida el cupón para una compra.
     *
     * @param base     valor de las líneas a las que aplica
     * @param redeemed el cupón ya cuenta un uso por esta orden (no se exige cupo otra vez)
     */
    public void check(LocalDateTime now, BigDecimal base, boolean redeemed) {
        if (!active) {
            throw new OrderRuleViolation("El cupón " + code + " no está activo.");
        }
        if (validFrom != null && now.isBefore(validFrom)) {
            throw new OrderRuleViolation("El cupón " + code + " todavía no está vigente.");
        }
        if (validUntil != null && now.isAfter(validUntil)) {
            throw new OrderRuleViolation("El cupón " + code + " está vencido.");
        }
        if (!redeemed && usageLimit != null && usageCount >= usageLimit) {
            throw new OrderRuleViolation("El cupón " + code + " ya agotó sus usos.");
        }
        if (base.signum() <= 0) {
            throw new OrderRuleViolation("El cupón " + code + " no aplica a ningún producto de la orden.");
        }
        if (minOrder != null && base.compareTo(minOrder) < 0) {
            throw new OrderRuleViolation("El cupón " + code + " exige una compra mínima de " + minOrder.toPlainString() + ".");
        }
    }

    /** Descuento sobre las líneas (el envío gratis se descuenta aparte). */
    public BigDecimal discountFor(BigDecimal base) {
        BigDecimal discount = switch (kind == null ? "" : kind) {
            case PERCENTAGE -> base.multiply(value).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            case FIXED_AMOUNT -> value;
            default -> BigDecimal.ZERO;
        };
        if (maxDiscount != null && maxDiscount.signum() > 0 && discount.compareTo(maxDiscount) > 0) {
            discount = maxDiscount;
        }
        return discount.min(base).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean freeShipping() {
        return FREE_SHIPPING.equals(kind);
    }
}
