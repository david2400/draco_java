package com.essenza.draco.modules.devolution.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Línea de una devolución: qué unidades de una línea de la orden se devuelven, cuántas
 * llegaron, en qué estado, si vuelven al stock y cuánto se acredita al cliente.
 */
public final class ReturnItem {

    private final Long id;
    private final Long productOrderId;
    private final Long productId;
    private final Long skuId;
    private final String skuCode;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final String observation;
    private Integer receivedQuantity;
    private ItemCondition condition;
    private BigDecimal restockingFee;
    private BigDecimal refundAmount;
    private Boolean restock;
    private int restockedQuantity;

    public ReturnItem(Long id, Long productOrderId, Long productId, Long skuId, String skuCode, String productName,
                      int quantity, BigDecimal unitPrice, String observation, Integer receivedQuantity,
                      ItemCondition condition, BigDecimal restockingFee, BigDecimal refundAmount, Boolean restock,
                      int restockedQuantity) {
        if (quantity <= 0) {
            throw new DevolutionRuleViolation("La cantidad a devolver debe ser mayor que cero.");
        }
        this.id = id;
        this.productOrderId = productOrderId;
        this.productId = productId;
        this.skuId = skuId;
        this.skuCode = skuCode;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice == null ? BigDecimal.ZERO.setScale(2) : unitPrice;
        this.observation = observation;
        this.receivedQuantity = receivedQuantity;
        this.condition = condition;
        this.restockingFee = restockingFee;
        this.refundAmount = refundAmount;
        this.restock = restock;
        this.restockedQuantity = restockedQuantity;
    }

    /** Nueva línea solicitada (aún sin recibir). */
    public static ReturnItem requested(Long productOrderId, Long productId, Long skuId, String skuCode, String productName,
                                       int quantity, BigDecimal unitPrice, String observation) {
        return new ReturnItem(null, productOrderId, productId, skuId, skuCode, productName, quantity, unitPrice,
                observation, null, null, null, null, null, 0);
    }

    void receive(int units) {
        if (units < 0 || units > quantity) {
            throw new DevolutionRuleViolation("\"" + label() + "\": se recibieron " + units + " de " + quantity
                    + " unidades solicitadas (debe estar entre 0 y " + quantity + ").");
        }
        this.receivedQuantity = units;
    }

    /**
     * Inspección: estado, reingreso al stock (por defecto según el estado) y cargo de reposición.
     * El valor acreditado es la parte proporcional del total de la línea de la orden menos el cargo.
     */
    void inspect(ItemCondition condition, Boolean restock, BigDecimal fee, BigDecimal orderLineTotal, int orderLineQuantity) {
        int units = received();
        BigDecimal value = proportional(orderLineTotal, orderLineQuantity, units);
        BigDecimal charge = fee == null ? BigDecimal.ZERO.setScale(2) : fee.setScale(2, RoundingMode.HALF_UP);
        if (charge.signum() < 0) {
            throw new DevolutionRuleViolation("\"" + label() + "\": el cargo de reposición no puede ser negativo.");
        }
        if (charge.compareTo(value) > 0) {
            throw new DevolutionRuleViolation("\"" + label() + "\": el cargo de reposición (" + charge.toPlainString()
                    + ") supera el valor devuelto (" + value.toPlainString() + ").");
        }
        if (units == 0) {
            this.condition = condition;
            this.restock = false;
            this.restockingFee = BigDecimal.ZERO.setScale(2);
            this.refundAmount = BigDecimal.ZERO.setScale(2);
            return;
        }
        this.condition = condition;
        this.restock = restock == null ? condition.restockByDefault() : restock;
        this.restockingFee = charge;
        this.refundAmount = value.subtract(charge);
    }

    void markRestocked() {
        this.restockedQuantity = Boolean.TRUE.equals(restock) ? received() : 0;
    }

    /** total × unidades / cantidad de la línea (redondeado; si se devuelve todo, el total exacto). */
    static BigDecimal proportional(BigDecimal lineTotal, int lineQuantity, int units) {
        if (lineTotal == null || lineQuantity <= 0 || units <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        if (units >= lineQuantity) {
            return lineTotal.setScale(2, RoundingMode.HALF_UP);
        }
        return lineTotal.multiply(BigDecimal.valueOf(units)).divide(BigDecimal.valueOf(lineQuantity), 2, RoundingMode.HALF_UP);
    }

    public int received() {
        return receivedQuantity == null ? 0 : receivedQuantity;
    }

    public String label() {
        return productName == null ? "Línea " + productOrderId : productName;
    }

    public Long id() { return id; }
    public Long productOrderId() { return productOrderId; }
    public Long productId() { return productId; }
    public Long skuId() { return skuId; }
    public String skuCode() { return skuCode; }
    public String productName() { return productName; }
    public int quantity() { return quantity; }
    public BigDecimal unitPrice() { return unitPrice; }
    public String observation() { return observation; }
    public Integer receivedQuantity() { return receivedQuantity; }
    public ItemCondition condition() { return condition; }
    public BigDecimal restockingFee() { return restockingFee; }
    public BigDecimal refundAmount() { return refundAmount; }
    public Boolean restock() { return restock; }
    public int restockedQuantity() { return restockedQuantity; }
}
