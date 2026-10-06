package com.essenza.draco.modules.sales.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.sales.domain.model.OrderLineAmounts;
import com.essenza.draco.modules.sales.domain.model.OrderStatus;

class OrderDomainTest {

    @Test
    void transicionesPermitidas() {
        assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.PAID)).isTrue();
        assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.SHIPPED)).isFalse();
        assertThat(OrderStatus.PAID.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
        assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.CANCELLED)).isFalse();
        assertThat(OrderStatus.DELIVERED.next()).isEmpty();
        assertThat(OrderStatus.CANCELLED.next()).isEmpty();
        assertThat(OrderStatus.PROCESSING.stockCommitted()).isTrue();
        assertThat(OrderStatus.PENDING.stockCommitted()).isFalse();
    }

    @Test
    void parseoTolerante() {
        assertThat(OrderStatus.tryParse(" paid ")).contains(OrderStatus.PAID);
        assertThat(OrderStatus.tryParse("pagada")).isEmpty();
        assertThatThrownBy(() -> OrderStatus.parse("pagada")).hasMessageContaining("Estado no válido");
    }

    @Test
    void importesDeLinea() {
        OrderLineAmounts amounts = OrderLineAmounts.of(new BigDecimal("35000"), 3, new BigDecimal("5000.004"));
        assertThat(amounts.subtotal()).isEqualByComparingTo("105000.00");
        assertThat(amounts.discount()).isEqualByComparingTo("5000.00");
        assertThat(amounts.total()).isEqualByComparingTo("100000.00");
        assertThat(OrderLineAmounts.of(new BigDecimal("10"), 1, null).total()).isEqualByComparingTo("10.00");
    }

    @Test
    void reglasDeImportes() {
        assertThatThrownBy(() -> OrderLineAmounts.of(BigDecimal.TEN, 0, null)).hasMessageContaining("cantidad");
        assertThatThrownBy(() -> OrderLineAmounts.of(BigDecimal.TEN, 1, new BigDecimal("11"))).hasMessageContaining("supera");
        assertThatThrownBy(() -> OrderLineAmounts.of(BigDecimal.TEN, 1, new BigDecimal("-1"))).hasMessageContaining("negativo");
    }
}
