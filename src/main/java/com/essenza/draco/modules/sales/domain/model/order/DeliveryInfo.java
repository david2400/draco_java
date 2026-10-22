package com.essenza.draco.modules.sales.domain.model.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Entrega de la orden: envío a una dirección (destinatario, dirección, ciudad del catálogo
 * geográfico, transportadora y costo) o retiro en una tienda (sin costo de envío).
 */
public record DeliveryInfo(DeliveryMethod method, Long pickupStoreId, String recipientName, String recipientPhone,
                           String address, String addressDetail, Long countryId, Long stateId, Long cityId,
                           String country, String state, String city, String postalCode, String notes,
                           Long carrierId, BigDecimal shippingAmount, LocalDate promisedDate) {

    public static final DeliveryInfo NONE = new DeliveryInfo(DeliveryMethod.SHIPPING, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, BigDecimal.ZERO.setScale(2), null);

    public static DeliveryInfo of(String method, Long pickupStoreId, String recipientName, String recipientPhone, String address,
                                  String addressDetail, Long countryId, Long stateId, Long cityId, String country, String state,
                                  String city, String postalCode, String notes, Long carrierId, BigDecimal shippingAmount,
                                  LocalDate promisedDate) {
        DeliveryMethod how = DeliveryMethod.parse(method);
        BigDecimal cost = shippingAmount == null ? BigDecimal.ZERO : shippingAmount;
        if (cost.signum() < 0) {
            throw new OrderRuleViolation("El costo de envío no puede ser negativo.");
        }
        if (cost.compareTo(new BigDecimal("100000000")) >= 0) {
            throw new OrderRuleViolation("El costo de envío es demasiado alto.");
        }
        String tel = Texts.clean(recipientPhone, 40, "Teléfono de quien recibe");
        if (how == DeliveryMethod.PICKUP) {
            if (pickupStoreId == null) {
                throw new OrderRuleViolation("Elige la tienda donde el cliente retira la orden.");
            }
            return new DeliveryInfo(how, pickupStoreId, Texts.clean(recipientName, 160, "Quién retira"), tel, null, null, null,
                    null, null, null, null, null, null, Texts.clean(notes, 500, "Notas de entrega"), null,
                    BigDecimal.ZERO.setScale(2), promisedDate);
        }
        return new DeliveryInfo(how, null, Texts.clean(recipientName, 160, "Quién recibe"), tel,
                Texts.clean(address, 200, "Dirección"), Texts.clean(addressDetail, 200, "Detalle de la dirección"),
                countryId, stateId, cityId, Texts.clean(country, 80, "País"), Texts.clean(state, 80, "Departamento"),
                Texts.clean(city, 120, "Ciudad"), Texts.clean(postalCode, 20, "Código postal"),
                Texts.clean(notes, 500, "Notas de entrega"), carrierId, cost.setScale(2, RoundingMode.HALF_UP), promisedDate);
    }

    /** Para despachar un envío hace falta a quién y a dónde. */
    public void requireShippingAddress() {
        if (method != DeliveryMethod.SHIPPING) {
            return;
        }
        if (recipientName == null) {
            throw new OrderRuleViolation("Indica quién recibe el envío.");
        }
        if (address == null) {
            throw new OrderRuleViolation("Indica la dirección de envío.");
        }
        if (cityId == null && city == null) {
            throw new OrderRuleViolation("Indica la ciudad de envío.");
        }
    }

    public boolean isPickup() {
        return method == DeliveryMethod.PICKUP;
    }
}
