package com.essenza.draco.modules.sales.domain.model.payment;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * Tipo de medio de pago. Define en qué canales tiene sentido y si un pago registrado a mano
 * queda aprobado de inmediato (el dinero ya se recibió) o pendiente de confirmación.
 */
public enum PaymentMethodKind {
    /** Efectivo: admite cambio. */
    CASH(EnumSet.of(PaymentChannel.PHYSICAL), true),
    /** Datáfono (tarjeta presente): número de aprobación del voucher. */
    CARD_TERMINAL(EnumSet.of(PaymentChannel.PHYSICAL), true),
    /** Transferencia o consignación: se confirma al verla en el banco. */
    BANK_TRANSFER(EnumSet.allOf(PaymentChannel.class), true),
    /** Nequi, Daviplata…: confirmación manual. */
    DIGITAL_WALLET(EnumSet.allOf(PaymentChannel.class), true),
    /** Pasarela en línea (Wompi, Mercado Pago, sandbox): la aprueba el webhook. */
    GATEWAY(EnumSet.of(PaymentChannel.VIRTUAL), false),
    /** Contraentrega: queda pendiente hasta que se cobra al entregar. */
    CASH_ON_DELIVERY(EnumSet.of(PaymentChannel.PHYSICAL), false),
    OTHER(EnumSet.allOf(PaymentChannel.class), true);

    private final Set<PaymentChannel> channels;
    private final boolean settlesOnRegister;

    PaymentMethodKind(Set<PaymentChannel> channels, boolean settlesOnRegister) {
        this.channels = channels;
        this.settlesOnRegister = settlesOnRegister;
    }

    public boolean allows(PaymentChannel channel) {
        return channels.contains(channel);
    }

    /** Un pago manual de este tipo queda aprobado al registrarlo (salvo que se pida dejarlo pendiente). */
    public boolean settlesOnRegister() {
        return settlesOnRegister;
    }

    public static PaymentMethodKind parse(String value) {
        if (value == null || value.isBlank()) {
            throw new PaymentRuleViolation("El tipo de medio de pago es obligatorio.");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new PaymentRuleViolation("Tipo de medio de pago no válido: " + value + ".");
        }
    }
}
