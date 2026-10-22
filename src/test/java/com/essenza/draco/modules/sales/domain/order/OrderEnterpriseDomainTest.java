package com.essenza.draco.modules.sales.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.sales.domain.model.OrderStatus;
import com.essenza.draco.modules.sales.domain.model.order.CouponRule;
import com.essenza.draco.modules.sales.domain.model.order.CustomerInfo;
import com.essenza.draco.modules.sales.domain.model.order.CustomerKind;
import com.essenza.draco.modules.sales.domain.model.order.DeliveryInfo;
import com.essenza.draco.modules.sales.domain.model.order.DiscountType;
import com.essenza.draco.modules.sales.domain.model.order.Fulfillment;
import com.essenza.draco.modules.sales.domain.model.order.Fulfillment.PickLine;
import com.essenza.draco.modules.sales.domain.model.order.OrderNumber;
import com.essenza.draco.modules.sales.domain.model.order.OrderPricing;
import com.essenza.draco.modules.sales.domain.model.order.OrderPricing.PricedLine;
import com.essenza.draco.modules.sales.domain.model.order.OrderRuleViolation;

class OrderEnterpriseDomainTest {

    static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 17, 12, 0);

    static BigDecimal d(String value) {
        return new BigDecimal(value);
    }

    static List<PricedLine> lines() {
        return List.of(new PricedLine(1L, 10L, 100L, d("60000.00")), new PricedLine(2L, 20L, 200L, d("40000.00")),
                new PricedLine(3L, 30L, 100L, d("0.01")));
    }

    static CouponRule coupon(String kind, String value, Set<Long> products, Set<Long> categories) {
        return new CouponRule(9L, "OFF", kind, d(value), null, null, NOW.minusDays(1), NOW.plusDays(1), true, 5, 0, products,
                Set.of(), categories, Set.of());
    }

    @Test
    void elRepartoCuadraAlCentavoYNoDejaLineasNegativas() {
        List<BigDecimal> shares = OrderPricing.allocate(List.of(d("33.33"), d("33.33"), d("33.34")), d("10.00"));
        assertThat(shares.stream().reduce(BigDecimal.ZERO, BigDecimal::add)).isEqualByComparingTo("10.00");
        assertThat(OrderPricing.allocate(List.of(d("5.00"), d("1.00")), d("50.00")))
                .containsExactly(d("5.00"), d("1.00"));
    }

    @Test
    void porcentajeYValorSeRepartenProporcionalmente() {
        OrderPricing.Result pct = OrderPricing.price(lines(), DiscountType.PERCENT, d("10"), null, false, d("8000"), NOW);
        assertThat(pct.discountAmount()).isEqualByComparingTo("10000.00");
        assertThat(pct.lineDiscounts().get(1L)).isEqualByComparingTo("6000.00");
        assertThat(pct.lineDiscounts().get(2L)).isEqualByComparingTo("4000.00");
        assertThat(pct.shippingDiscount()).isEqualByComparingTo("0");

        assertThatThrownBy(() -> OrderPricing.price(lines(), DiscountType.AMOUNT, d("200000"), null, false, null, NOW))
                .isInstanceOf(OrderRuleViolation.class).hasMessageContaining("supera");
        assertThatThrownBy(() -> OrderPricing.price(lines(), DiscountType.PERCENT, d("120"), null, false, null, NOW))
                .isInstanceOf(OrderRuleViolation.class);
    }

    @Test
    void elCuponSoloAplicaALasLineasQueCubreYValidaVigenciaYCupo() {
        CouponRule byCategory = coupon(CouponRule.PERCENTAGE, "50", Set.of(), Set.of(200L));
        OrderPricing.Result result = OrderPricing.price(lines(), DiscountType.COUPON, null, byCategory, false, d("8000"), NOW);
        assertThat(result.lineDiscounts().get(1L)).isEqualByComparingTo("0");
        assertThat(result.lineDiscounts().get(2L)).isEqualByComparingTo("20000.00");

        CouponRule exhausted = new CouponRule(9L, "OFF", CouponRule.FIXED_AMOUNT, d("5000"), null, null, null, null, true, 1, 1,
                Set.of(), Set.of(), Set.of(), Set.of());
        assertThatThrownBy(() -> OrderPricing.price(lines(), DiscountType.COUPON, null, exhausted, false, null, NOW))
                .hasMessageContaining("agotó");
        // Una orden ya pagada cuenta su propio uso: no se le exige cupo otra vez.
        assertThat(OrderPricing.price(lines(), DiscountType.COUPON, null, exhausted, true, null, NOW).discountAmount())
                .isEqualByComparingTo("5000");

        CouponRule expired = new CouponRule(9L, "OFF", CouponRule.FIXED_AMOUNT, d("5000"), null, null, null, NOW.minusHours(1),
                true, null, 0, Set.of(), Set.of(), Set.of(), Set.of());
        assertThatThrownBy(() -> OrderPricing.price(lines(), DiscountType.COUPON, null, expired, false, null, NOW))
                .hasMessageContaining("vencido");
        CouponRule other = coupon(CouponRule.PERCENTAGE, "10", Set.of(999L), Set.of());
        assertThatThrownBy(() -> OrderPricing.price(lines(), DiscountType.COUPON, null, other, false, null, NOW))
                .hasMessageContaining("no aplica");
    }

    @Test
    void envioGratisDescuentaElCostoDeEnvio() {
        CouponRule free = coupon(CouponRule.FREE_SHIPPING, "0", Set.of(), Set.of());
        OrderPricing.Result result = OrderPricing.price(lines(), DiscountType.COUPON, null, free, false, d("12000"), NOW);
        assertThat(result.discountAmount()).isEqualByComparingTo("0");
        assertThat(result.shippingDiscount()).isEqualByComparingTo("12000.00");
    }

    @Test
    void clienteNormalizaDocumentoCorreoYTipo() {
        CustomerInfo c = CustomerInfo.of(null, null, "nit", "900.123.456-7", "  Acme   SAS ", "VENTAS@ACME.CO", "+57 300 1234567",
                null, null);
        assertThat(c.docNumber()).isEqualTo("900123456-7");
        assertThat(c.kind()).isEqualTo(CustomerKind.COMPANY);
        assertThat(c.name()).isEqualTo("Acme SAS");
        assertThat(c.email()).isEqualTo("ventas@acme.co");
        assertThatThrownBy(() -> CustomerInfo.of(null, null, "XX", "123", "A", null, null, null, null))
                .hasMessageContaining("Tipo de documento");
        assertThatThrownBy(() -> CustomerInfo.of(null, null, null, null, "A", "no-es-correo", null, null, null))
                .hasMessageContaining("correo");
        assertThatThrownBy(() -> CustomerInfo.of(null, null, null, null, "Ana", null, null, null, null).requireContact())
                .hasMessageContaining("correo o el teléfono");
    }

    @Test
    void entregaExigeDireccionOTiendaYElRetiroNoCobraEnvio() {
        DeliveryInfo pickup = DeliveryInfo.of("PICKUP", 3L, null, null, "Calle 1", null, null, null, null, null, null, null, null,
                null, 4L, d("9000"), null);
        assertThat(pickup.shippingAmount()).isEqualByComparingTo("0");
        assertThat(pickup.address()).isNull();
        assertThatThrownBy(() -> DeliveryInfo.of("PICKUP", null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null)).hasMessageContaining("tienda");
        DeliveryInfo noAddress = DeliveryInfo.of("SHIPPING", null, "Ana", null, null, null, null, null, null, null, null, null,
                null, null, null, d("8000"), null);
        assertThatThrownBy(noAddress::requireShippingAddress).hasMessageContaining("dirección");
        assertThatThrownBy(() -> DeliveryInfo.of("SHIPPING", null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, d("-1"), null)).hasMessageContaining("negativo");
    }

    @Test
    void numeracionConPrefijoYRelleno() {
        OrderNumber n = new OrderNumber("ord", 6, 42);
        assertThat(n.current()).isEqualTo("ORD-000042");
        assertThat(n.advance().current()).isEqualTo("ORD-000043");
        assertThat(new OrderNumber("WEB", 2, 1234).current()).isEqualTo("WEB-1234");
        assertThatThrownBy(() -> new OrderNumber("OR D", 6, 1)).isInstanceOf(OrderRuleViolation.class);
    }

    @Test
    void preparacionDespachoYEntrega() {
        PickLine line = new PickLine(1L, "SKU-1", 3, 1);
        assertThat(Fulfillment.checkPicked(line, 3)).isEqualTo(3);
        assertThatThrownBy(() -> Fulfillment.checkPicked(line, 4)).hasMessageContaining("se pidieron 3");
        assertThatThrownBy(() -> Fulfillment.requireAllPicked(List.of(line))).hasMessageContaining("SKU-1 (1/3)");
        assertThatThrownBy(() -> Fulfillment.checkCanPick(OrderStatus.PENDING)).isInstanceOf(OrderRuleViolation.class);

        DeliveryInfo shipping = DeliveryInfo.of("SHIPPING", null, "Ana", "3001234567", "Calle 1 # 2-3", null, 1L, 2L, 3L, null,
                null, "Medellín", null, null, null, d("8000"), null);
        assertThatThrownBy(() -> Fulfillment.checkCanShip(OrderStatus.PROCESSING, shipping, 5L, " "))
                .hasMessageContaining("guía");
        Fulfillment.checkCanShip(OrderStatus.READY, shipping, 5L, "G-1");
        assertThatThrownBy(() -> Fulfillment.checkCanDeliver(OrderStatus.READY, shipping)).hasMessageContaining("despachan");

        assertThat(OrderStatus.PAID.canTransitionTo(OrderStatus.READY)).isTrue();
        assertThat(OrderStatus.READY.canTransitionTo(OrderStatus.DELIVERED)).isTrue();
        assertThat(OrderStatus.READY.stockCommitted()).isTrue();
        assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.READY)).isFalse();
    }
}
