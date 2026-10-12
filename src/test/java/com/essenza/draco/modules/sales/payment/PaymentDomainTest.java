package com.essenza.draco.modules.sales.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.sales.domain.model.payment.GatewayProvider;
import com.essenza.draco.modules.sales.domain.model.payment.OrderPaymentStatus;
import com.essenza.draco.modules.sales.domain.model.payment.Payment;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentChannel;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethodKind;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentRuleViolation;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentStatus;

class PaymentDomainTest {

    static PaymentMethod method(String code, PaymentChannel channel, PaymentMethodKind kind, GatewayProvider provider,
                                boolean allowsChange) {
        return new PaymentMethod(null, code, code, null, null, channel, kind, provider, true, 0, new BigDecimal("2.99"),
                new BigDecimal("900"), null, null, false, allowsChange);
    }

    static Payment payment(PaymentStatus status, String amount) {
        return new Payment(1L, 10L, 2L, PaymentChannel.PHYSICAL, PaymentMethodKind.CASH, null, status, new BigDecimal(amount),
                BigDecimal.ZERO, null, null, BigDecimal.ZERO, null, "PAY-10-ABCDEFGH", null, null, null, null, null, null,
                null, null, Instant.now());
    }

    @Test
    void elMedioValidaCanalTipoYPasarela() {
        assertThatThrownBy(() -> method("CASH", PaymentChannel.VIRTUAL, PaymentMethodKind.CASH, null, true))
                .isInstanceOf(PaymentRuleViolation.class).hasMessageContaining("canal");
        assertThatThrownBy(() -> method("WOMPI", PaymentChannel.VIRTUAL, PaymentMethodKind.GATEWAY, null, false))
                .hasMessageContaining("proveedor");
        assertThatThrownBy(() -> method("CARD", PaymentChannel.PHYSICAL, PaymentMethodKind.CARD_TERMINAL, null, true))
                .hasMessageContaining("cambio");
        assertThatThrownBy(() -> method("bad code", PaymentChannel.PHYSICAL, PaymentMethodKind.CASH, null, false))
                .hasMessageContaining("código");
        // Un medio que no es pasarela no conserva proveedor.
        assertThat(method("BANK", PaymentChannel.VIRTUAL, PaymentMethodKind.BANK_TRANSFER, GatewayProvider.WOMPI, false).provider())
                .isNull();
    }

    @Test
    void laComisionSumaPorcentajeYFija() {
        PaymentMethod wompi = method("WOMPI", PaymentChannel.VIRTUAL, PaymentMethodKind.GATEWAY, GatewayProvider.WOMPI, false);
        assertThat(wompi.feeFor(new BigDecimal("100000"))).isEqualByComparingTo("3890.00");
    }

    @Test
    void losLimitesDelMedioSeRespetan() {
        PaymentMethod limited = new PaymentMethod(null, "COD", "Contraentrega", null, null, PaymentChannel.PHYSICAL,
                PaymentMethodKind.CASH_ON_DELIVERY, null, true, 0, null, null, new BigDecimal("1000"), new BigDecimal("500000"),
                false, true);
        assertThatThrownBy(() -> limited.checkAmount(new BigDecimal("600000"))).hasMessageContaining("hasta");
        assertThatThrownBy(() -> limited.checkAmount(new BigDecimal("500"))).hasMessageContaining("desde");
        limited.checkAmount(new BigDecimal("50000"));
    }

    @Test
    void reembolsoParcialYTotal() {
        Payment p = payment(PaymentStatus.APPROVED, "50000");
        p.refund(new BigDecimal("20000"), "Devolución parcial");
        assertThat(p.getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(p.settledAmount()).isEqualByComparingTo("30000");
        assertThatThrownBy(() -> p.voidPayment("x")).hasMessageContaining("reembolsos");
        assertThatThrownBy(() -> p.refund(new BigDecimal("40000"), null)).hasMessageContaining("máximo");
        p.refund(new BigDecimal("30000"), null);
        assertThat(p.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(p.settledAmount()).isZero();
    }

    @Test
    void transicionesInvalidasSeRechazan() {
        Payment declined = payment(PaymentStatus.DECLINED, "1000");
        assertThatThrownBy(() -> declined.approve(Instant.now(), null, null)).isInstanceOf(PaymentRuleViolation.class);
        Payment pending = payment(PaymentStatus.PENDING, "1000");
        assertThatThrownBy(() -> pending.refund(BigDecimal.ONE, null)).hasMessageContaining("aprobados");
        pending.approve(Instant.now(), "TX-1", "ok");
        assertThat(pending.getProviderReference()).isEqualTo("TX-1");
        assertThat(pending.getApprovedAt()).isNotNull();
    }

    @Test
    void saldoDeLaOrden() {
        BigDecimal total = new BigDecimal("100000");
        var partial = OrderPaymentStatus.of(total, List.of(payment(PaymentStatus.APPROVED, "40000"),
                payment(PaymentStatus.PENDING, "30000"), payment(PaymentStatus.DECLINED, "99999")));
        assertThat(partial.status()).isEqualTo(OrderPaymentStatus.PARTIAL);
        assertThat(partial.balance()).isEqualByComparingTo("60000");
        assertThat(partial.collectable()).isEqualByComparingTo("30000");

        var paid = OrderPaymentStatus.of(total, List.of(payment(PaymentStatus.APPROVED, "100000")));
        assertThat(paid.status()).isEqualTo(OrderPaymentStatus.PAID);
        assertThat(paid.collectable()).isZero();

        assertThat(OrderPaymentStatus.of(total, List.of()).status()).isEqualTo(OrderPaymentStatus.UNPAID);
        assertThat(OrderPaymentStatus.of(total, List.of(payment(PaymentStatus.APPROVED, "120000"))).status())
                .isEqualTo(OrderPaymentStatus.OVERPAID);
    }
}
