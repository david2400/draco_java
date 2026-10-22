package com.essenza.draco.modules.sales.application.services.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.essenza.draco.modules.sales.application.dto.order.ChangeOrderStatusDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.payment.RegisterPaymentDto;
import com.essenza.draco.modules.sales.application.dto.payment.StartCheckoutDto;
import com.essenza.draco.modules.sales.application.input.order.ChangeOrderStatusUseCase;
import com.essenza.draco.modules.sales.application.input.order.FindOrderByIdUseCase;
import com.essenza.draco.modules.sales.application.output.gateway.PaymentGateway;
import com.essenza.draco.modules.sales.application.output.gateway.PaymentGateways;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger;
import com.essenza.draco.modules.sales.application.output.repository.PaymentEventLog;
import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;
import com.essenza.draco.modules.sales.application.output.repository.PaymentRepository;
import com.essenza.draco.modules.sales.domain.model.payment.GatewayProvider;
import com.essenza.draco.modules.sales.domain.model.payment.Payment;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentChannel;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethodKind;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentRuleViolation;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServicesTest {

    @Mock PaymentRepository payments;
    @Mock PaymentMethodRepository methods;
    @Mock PaymentEventLog events;
    @Mock PaymentGateways gateways;
    @Mock PaymentGateway sandbox;
    @Mock OrderLedger ledger;
    @Mock FindOrderByIdUseCase findOrder;
    @Mock ChangeOrderStatusUseCase changeStatus;

    PaymentSoporte soporte;
    RegisterPaymentService register;
    StartCheckoutService checkout;
    HandleGatewayWebhookService webhook;

    final List<Payment> stored = new ArrayList<>();
    String orderState = "PENDING";

    static final PaymentMethod CASH = new PaymentMethod(1L, "CASH", "Efectivo", null, null, PaymentChannel.PHYSICAL,
            PaymentMethodKind.CASH, null, true, 0, null, null, null, null, false, true);
    static final PaymentMethod CARD = new PaymentMethod(2L, "CARD_TERMINAL", "Datáfono", null, null, PaymentChannel.PHYSICAL,
            PaymentMethodKind.CARD_TERMINAL, null, true, 0, null, null, null, null, true, false);
    static final PaymentMethod TRANSFER = new PaymentMethod(3L, "BANK_TRANSFER", "Transferencia", null, null,
            PaymentChannel.VIRTUAL, PaymentMethodKind.BANK_TRANSFER, null, true, 0, null, null, null, null, false, false);
    static final PaymentMethod SANDBOX = new PaymentMethod(4L, "SANDBOX", "Pruebas", null, null, PaymentChannel.VIRTUAL,
            PaymentMethodKind.GATEWAY, GatewayProvider.SANDBOX, true, 0, null, null, null, null, false, false);

    @BeforeEach
    void setUp() {
        soporte = new PaymentSoporte(payments, methods, events, gateways, ledger, findOrder, changeStatus);
        register = new RegisterPaymentService(payments, soporte,
                org.mockito.Mockito.mock(com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository.class));
        checkout = new StartCheckoutService(payments, gateways, soporte);
        webhook = new HandleGatewayWebhookService(payments, gateways, events, soporte);
        when(ledger.lockOrder(anyLong())).thenReturn(Optional.of(new OrderLedger.OrderHeader(10L, "PENDING", true, Instant.now())));
        when(ledger.lines(10L)).thenReturn(List.of(new OrderLedger.OrderLine(1L, 10L, 1L, 1L, "SKU", "P", BigDecimal.TEN, 1,
                BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.TEN)));
        when(findOrder.findById(10L)).thenAnswer(inv -> Optional.of(OrderDto.builder().id(10L).state(orderState)
                .total(new BigDecimal("100000")).build()));
        for (PaymentMethod m : List.of(CASH, CARD, TRANSFER, SANDBOX)) {
            when(methods.findById(m.id())).thenReturn(Optional.of(m));
        }
        when(methods.findAll()).thenReturn(List.of(CASH, CARD, TRANSFER, SANDBOX));
        when(payments.findByOrderId(10L)).thenAnswer(inv -> List.copyOf(stored));
        when(payments.save(any())).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            Payment saved = p.getId() != null ? p : new Payment((long) stored.size() + 1, p.getOrderId(), p.getMethodId(),
                    p.getChannel(), p.getKind(), p.getProvider(), p.getStatus(), p.getAmount(), p.getFeeAmount(),
                    p.getTenderedAmount(), p.getChangeAmount(), p.getRefundedAmount(), p.getReference(), p.getPaymentReference(),
                    p.getProviderReference(), p.getCheckoutUrl(), p.getReturnUrl(), p.getPayerEmail(), p.getNotes(),
                    p.getStatusDetail(), p.getApprovedAt(), p.getExpiresAt(), p.getCreatedAt());
            stored.removeIf(x -> x.getId().equals(saved.getId()));
            stored.add(saved);
            return saved;
        });
        when(gateways.find(GatewayProvider.SANDBOX)).thenReturn(Optional.of(sandbox));
        when(gateways.defaultReturnUrl()).thenReturn("https://tienda.co/ok");
        when(sandbox.configured()).thenReturn(true);
        when(sandbox.provider()).thenReturn(GatewayProvider.SANDBOX);
    }

    @Test
    void efectivoConCambioCubreLaOrdenYLaMarcaPagada() {
        var dto = register.register(new RegisterPaymentDto(10L, 1L, new BigDecimal("100000"), new BigDecimal("120000"), null, null, null));
        assertThat(dto.status()).isEqualTo("APPROVED");
        assertThat(dto.changeAmount()).isEqualByComparingTo("20000");
        ArgumentCaptor<ChangeOrderStatusDto> status = ArgumentCaptor.forClass(ChangeOrderStatusDto.class);
        verify(changeStatus).changeStatus(eq(10L), status.capture());
        assertThat(status.getValue().state()).isEqualTo("PAID");
    }

    @Test
    void pagoMixtoParcialNoMarcaPagadaHastaCompletar() {
        register.register(new RegisterPaymentDto(10L, 1L, new BigDecimal("40000"), null, null, null, null));
        verify(changeStatus, never()).changeStatus(anyLong(), any());
        register.register(new RegisterPaymentDto(10L, 2L, new BigDecimal("60000"), null, "APR-123", null, null));
        verify(changeStatus).changeStatus(eq(10L), any());
    }

    @Test
    void reglasDeCaptura() {
        assertThatThrownBy(() -> register.register(new RegisterPaymentDto(10L, 2L, new BigDecimal("1000"), null, null, null, null)))
                .isInstanceOf(PaymentRuleViolation.class).hasMessageContaining("referencia");
        assertThatThrownBy(() -> register.register(new RegisterPaymentDto(10L, 2L, new BigDecimal("1000"), new BigDecimal("2000"), "A", null, null)))
                .hasMessageContaining("no admite cambio");
        assertThatThrownBy(() -> register.register(new RegisterPaymentDto(10L, 1L, new BigDecimal("150000"), null, null, null, null)))
                .hasMessageContaining("supera");
        assertThatThrownBy(() -> register.register(new RegisterPaymentDto(10L, 4L, new BigDecimal("1000"), null, null, null, null)))
                .hasMessageContaining("checkout");
    }

    @Test
    void transferenciaPendienteReservaElSaldo() {
        var dto = register.register(new RegisterPaymentDto(10L, 3L, new BigDecimal("100000"), null, null, null, true));
        assertThat(dto.status()).isEqualTo("PENDING");
        assertThatThrownBy(() -> register.register(new RegisterPaymentDto(10L, 1L, new BigDecimal("1000"), null, null, null, null)))
                .hasMessageContaining("pendientes");
    }

    @Test
    void ordenCanceladaNoAdmitePagos() {
        orderState = "CANCELLED";
        assertThatThrownBy(() -> register.register(new RegisterPaymentDto(10L, 1L, new BigDecimal("1000"), null, null, null, null)))
                .hasMessageContaining("cancelada");
    }

    @Test
    void checkoutYWebhookAprueban() {
        when(sandbox.createCheckout(any())).thenReturn(new PaymentGateway.CheckoutSession("https://pay/x", null));
        var result = checkout.start(new StartCheckoutDto(10L, 4L, null, "a@b.co", null));
        assertThat(result.checkoutUrl()).isEqualTo("https://pay/x");
        assertThat(result.payment().amount()).isEqualByComparingTo("100000");
        assertThat(result.payment().status()).isEqualTo("PENDING");
        String ref = result.payment().paymentReference();

        Payment pending = stored.get(0);
        when(payments.lockByPaymentReference(ref)).thenReturn(Optional.of(pending));
        when(sandbox.parseWebhook(any(), any(), any())).thenReturn(new PaymentGateway.WebhookEvent("TX1:APPROVED", ref, "TX1",
                PaymentStatus.APPROVED, new BigDecimal("100000"), "ok", false));
        assertThat(webhook.handle("sandbox", Map.of(), Map.of(), "{}")).isTrue();
        assertThat(stored.get(0).getStatus()).isEqualTo(PaymentStatus.APPROVED);
        verify(changeStatus).changeStatus(eq(10L), any());

        // La misma notificación otra vez no se aplica.
        when(events.exists("SANDBOX", "TX1:APPROVED")).thenReturn(true);
        assertThat(webhook.handle("sandbox", Map.of(), Map.of(), "{}")).isFalse();
    }

    @Test
    void webhookConMontoDistintoNoAprueba() {
        when(sandbox.createCheckout(any())).thenReturn(new PaymentGateway.CheckoutSession("https://pay/x", null));
        var result = checkout.start(new StartCheckoutDto(10L, 4L, new BigDecimal("50000"), null, null));
        Payment pending = stored.get(0);
        when(payments.lockByPaymentReference(any())).thenReturn(Optional.of(pending));
        when(sandbox.parseWebhook(any(), any(), any())).thenReturn(new PaymentGateway.WebhookEvent("TX2:APPROVED",
                result.payment().paymentReference(), "TX2", PaymentStatus.APPROVED, new BigDecimal("100"), "ok", false));
        assertThat(webhook.handle("sandbox", Map.of(), Map.of(), "{}")).isFalse();
        assertThat(stored.get(0).getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(stored.get(0).getStatusDetail()).contains("distinto");
        verify(changeStatus, never()).changeStatus(anyLong(), any());
    }

    @Test
    void pasarelaSinCredencialesNoSeUsa() {
        when(sandbox.configured()).thenReturn(false);
        assertThatThrownBy(() -> checkout.start(new StartCheckoutDto(10L, 4L, null, null, null)))
                .hasMessageContaining("no está configurada");
    }

    @Test
    void unNuevoCheckoutReemplazaElAnteriorYLaAprobacionTardiaSeAcepta() {
        when(sandbox.createCheckout(any())).thenReturn(new PaymentGateway.CheckoutSession("https://pay/1", null));
        var first = checkout.start(new StartCheckoutDto(10L, 4L, null, null, null));
        var second = checkout.start(new StartCheckoutDto(10L, 4L, null, null, null));
        assertThat(stored).hasSize(2);
        assertThat(stored.get(0).getStatus()).isEqualTo(PaymentStatus.EXPIRED);
        assertThat(stored.get(1).getStatus()).isEqualTo(PaymentStatus.PENDING);

        // El cliente sí pagó el primer link: la pasarela lo confirma tarde y se acepta.
        Payment old = stored.get(0);
        when(payments.lockByPaymentReference(first.payment().paymentReference())).thenReturn(Optional.of(old));
        when(sandbox.parseWebhook(any(), any(), any())).thenReturn(new PaymentGateway.WebhookEvent("TX9:APPROVED",
                first.payment().paymentReference(), "TX9", PaymentStatus.APPROVED, new BigDecimal("100000"), "ok", false));
        assertThat(webhook.handle("sandbox", Map.of(), Map.of(), "{}")).isTrue();
        assertThat(old.getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(second.payment().status()).isEqualTo("PENDING");
    }
}
