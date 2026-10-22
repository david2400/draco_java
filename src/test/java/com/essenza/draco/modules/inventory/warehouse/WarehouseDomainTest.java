package com.essenza.draco.modules.inventory.warehouse;

import com.essenza.draco.modules.inventory.domain.model.StockItem;
import com.essenza.draco.modules.inventory.domain.model.warehouse.LocationType;
import com.essenza.draco.modules.inventory.domain.model.warehouse.Warehouse;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseCapacity;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseLocation;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseOperation;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WarehouseDomainTest {

    private static Warehouse warehouse(WarehouseType type, boolean main, boolean active, WarehouseOperation op) {
        return new Warehouse(null, "bod-1", "Central", type, main, active, op, null, null, null, null, null, null, null,
                null, null, null, null);
    }

    @Test
    void reglasDeTipoYPrincipal() {
        Warehouse w = warehouse(null, false, true, null);
        assertThat(w.code()).isEqualTo("BOD-1");
        assertThat(w.type()).isEqualTo(WarehouseType.ALMACEN);
        assertThat(w.operation().sales()).isTrue();
        assertThatThrownBy(() -> warehouse(WarehouseType.TRANSITO, false, true, WarehouseOperation.ALL)).hasMessageContaining("tránsito");
        assertThat(warehouse(WarehouseType.TRANSITO, false, true, new WarehouseOperation(false, true, true, 5)).operation().priority()).isEqualTo(5);
        assertThatThrownBy(() -> warehouse(WarehouseType.DEVOLUCIONES, true, true, null)).hasMessageContaining("principal");
        assertThatThrownBy(() -> warehouse(WarehouseType.ALMACEN, true, false, null)).hasMessageContaining("desactivar");
        assertThat(w.asMain(true).main()).isTrue();
        assertThatThrownBy(() -> new WarehouseOperation(true, true, true, 0)).hasMessageContaining("prioridad");
        assertThatThrownBy(() -> WarehouseType.of("BODEGA")).hasMessageContaining("Tipo de bodega");
    }

    @Test
    void capacidadYOcupacion() {
        WarehouseCapacity cap = new WarehouseCapacity(new BigDecimal("120.5"), 1000, 40);
        assertThat(cap.occupancy(523)).isEqualByComparingTo("52.3");
        assertThat(WarehouseCapacity.NONE.occupancy(10)).isNull();
        assertThatThrownBy(() -> new WarehouseCapacity(BigDecimal.ZERO, null, null)).hasMessageContaining("área");
        assertThatThrownBy(() -> new WarehouseCapacity(null, -1, null)).hasMessageContaining("unidades");
    }

    @Test
    void ubicaciones() {
        WarehouseLocation l = new WarehouseLocation(null, 1L, null, "a", "01", "03", "2", null, null, true);
        assertThat(l.code()).isEqualTo("A-01-03-2");
        assertThat(l.type()).isEqualTo(LocationType.STORAGE);
        assertThatThrownBy(() -> new WarehouseLocation(null, 1L, null, null, null, null, null, null, null, true))
                .hasMessageContaining("zona");
        assertThatThrownBy(() -> new WarehouseLocation(null, 1L, "a b", null, null, null, null, null, null, true))
                .hasMessageContaining("código");
        assertThatThrownBy(() -> LocationType.of("SHELF")).hasMessageContaining("ubicación");
    }

    @Test
    void minimoDelSku() {
        StockItem item = new StockItem(null, 1L, 1L, 1L, 10, 4, 0);
        item.changeMinThreshold(6);
        assertThat(item.isBelowThreshold()).isTrue();
        assertThatThrownBy(() -> item.changeMinThreshold(-1)).hasMessageContaining("negativo");
    }
}
