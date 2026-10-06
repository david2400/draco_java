package com.essenza.draco.modules.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.essenza.draco.modules.inventory.domain.model.InsufficientStockException;
import com.essenza.draco.modules.inventory.domain.model.StockItem;
import org.junit.jupiter.api.Test;

class StockItemTest {

    @Test
    void entradaYSalidaAjustanLasUnidadesFisicas() {
        StockItem item = StockItem.empty(1L, 10L, 100L);
        item.receive(8);
        item.issue(3);
        assertThat(item.getOnHand()).isEqualTo(5);
        assertThat(item.available()).isEqualTo(5);
    }

    @Test
    void noSePuedeSacarMasDeLoDisponible() {
        StockItem item = new StockItem(1L, 1L, 10L, 100L, 5, 2, 0);
        assertThatThrownBy(() -> item.issue(4))
                .isInstanceOf(InsufficientStockException.class)
                .extracting("available", "requested").containsExactly(3, 4);
        assertThat(item.getOnHand()).isEqualTo(5);
    }

    @Test
    void cantidadesInvalidas() {
        StockItem item = StockItem.empty(1L, 10L, 100L);
        assertThatThrownBy(() -> item.receive(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StockItem(null, 1L, 1L, 1L, 1, 2, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StockItem(null, 1L, 1L, 1L, -1, 0, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void alertaDeStockMinimo() {
        assertThat(new StockItem(1L, 1L, 1L, 1L, 3, 0, 5).isBelowThreshold()).isTrue();
        assertThat(new StockItem(1L, 1L, 1L, 1L, 9, 0, 5).isBelowThreshold()).isFalse();
        assertThat(new StockItem(1L, 1L, 1L, 1L, 0, 0, 0).isBelowThreshold()).isFalse();
    }
}
