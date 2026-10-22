package com.essenza.draco.modules.sales.domain.model.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Descuento global de la orden (porcentaje, valor o cupón) repartido entre las líneas en
 * proporción a su valor, para que el impuesto de cada línea se calcule sobre lo que de
 * verdad se cobra. El envío gratis de un cupón descuenta el costo de envío.
 */
public final class OrderPricing {

    private OrderPricing() {
    }

    /** Línea a precio: valor después de su propio descuento. */
    public record PricedLine(Long lineId, Long productId, Long categoryId, BigDecimal amount) {
    }

    /** Descuento de cada línea, total de líneas descontado y descuento del envío. */
    public record Result(Map<Long, BigDecimal> lineDiscounts, BigDecimal discountAmount, BigDecimal shippingDiscount) {
    }

    public static Result price(List<PricedLine> lines, DiscountType type, BigDecimal value, CouponRule coupon,
                               boolean couponRedeemed, BigDecimal shippingAmount, LocalDateTime now) {
        BigDecimal shipping = shippingAmount == null ? BigDecimal.ZERO : shippingAmount;
        List<PricedLine> eligible = new ArrayList<>();
        BigDecimal base = BigDecimal.ZERO;
        for (PricedLine line : lines) {
            if (type != DiscountType.COUPON || coupon == null || coupon.covers(line.productId(), line.categoryId())) {
                eligible.add(line);
                base = base.add(line.amount());
            }
        }
        BigDecimal discount;
        BigDecimal shippingDiscount = BigDecimal.ZERO.setScale(2);
        switch (type) {
            case NONE -> discount = BigDecimal.ZERO;
            case PERCENT -> {
                if (value == null || value.signum() <= 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
                    throw new OrderRuleViolation("El descuento en porcentaje va de 0,01 a 100.");
                }
                discount = base.multiply(value).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }
            case AMOUNT -> {
                if (value == null || value.signum() <= 0) {
                    throw new OrderRuleViolation("Indica el valor del descuento (mayor que 0).");
                }
                if (value.compareTo(base) > 0) {
                    throw new OrderRuleViolation("El descuento (" + value.toPlainString() + ") supera el valor de los productos ("
                            + base.toPlainString() + ").");
                }
                discount = value;
            }
            case COUPON -> {
                if (coupon == null) {
                    throw new OrderRuleViolation("El cupón no existe.");
                }
                coupon.check(now, base, couponRedeemed);
                discount = coupon.discountFor(base);
                if (coupon.freeShipping()) {
                    shippingDiscount = shipping.setScale(2, RoundingMode.HALF_UP);
                }
            }
            default -> throw new IllegalStateException();
        }
        Map<Long, BigDecimal> perLine = new LinkedHashMap<>();
        lines.forEach(line -> perLine.put(line.lineId(), BigDecimal.ZERO.setScale(2)));
        List<BigDecimal> shares = allocate(eligible.stream().map(PricedLine::amount).toList(), discount);
        for (int i = 0; i < eligible.size(); i++) {
            perLine.put(eligible.get(i).lineId(), shares.get(i));
        }
        return new Result(perLine, discount.setScale(2, RoundingMode.HALF_UP), shippingDiscount);
    }

    /**
     * Reparte {@code discount} en proporción a {@code amounts} (2 decimales). Los centavos que
     * sobran del redondeo van a las líneas de mayor valor; ninguna línea queda negativa.
     */
    public static List<BigDecimal> allocate(List<BigDecimal> amounts, BigDecimal discount) {
        List<BigDecimal> shares = new ArrayList<>();
        BigDecimal total = amounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal target = discount == null ? BigDecimal.ZERO : discount.setScale(2, RoundingMode.HALF_UP);
        if (amounts.isEmpty()) {
            return shares;
        }
        if (total.signum() <= 0 || target.signum() <= 0) {
            amounts.forEach(a -> shares.add(BigDecimal.ZERO.setScale(2)));
            return shares;
        }
        target = target.min(total);
        BigDecimal assigned = BigDecimal.ZERO;
        for (BigDecimal amount : amounts) {
            BigDecimal share = amount.multiply(target).divide(total, 2, RoundingMode.DOWN);
            shares.add(share);
            assigned = assigned.add(share);
        }
        BigDecimal cent = new BigDecimal("0.01");
        BigDecimal rest = target.subtract(assigned);
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < amounts.size(); i++) {
            order.add(i);
        }
        order.sort((a, b) -> amounts.get(b).compareTo(amounts.get(a)));
        while (rest.signum() > 0) {
            boolean moved = false;
            for (int index : order) {
                if (rest.signum() <= 0) {
                    break;
                }
                if (shares.get(index).add(cent).compareTo(amounts.get(index)) <= 0) {
                    shares.set(index, shares.get(index).add(cent));
                    rest = rest.subtract(cent);
                    moved = true;
                }
            }
            if (!moved) {
                break;
            }
        }
        return shares;
    }
}
