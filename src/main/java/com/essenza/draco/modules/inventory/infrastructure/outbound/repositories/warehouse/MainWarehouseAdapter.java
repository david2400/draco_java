package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.warehouse;

import com.essenza.draco.modules.inventory.application.output.repository.MainWarehousePort;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.WarehouseEntity;
import org.springframework.stereotype.Component;

/**
 * Bodega principal: la marcada {@code is_main}; si no hay, la primera activa
 * (y queda marcada); si no existe ninguna bodega, se crea "Bodega principal".
 */
@Component
public class MainWarehouseAdapter implements MainWarehousePort {

    static final String DEFAULT_CODE = "PRINCIPAL";

    private final JpaWarehouseRepository warehouses;

    public MainWarehouseAdapter(JpaWarehouseRepository warehouses) {
        this.warehouses = warehouses;
    }

    @Override
    public Long mainWarehouseId() {
        return warehouses.findFirstByIsMainTrueOrderByIdAsc()
                .or(() -> warehouses.findFirstByActiveTrueOrderByIdAsc().map(w -> {
                    w.setIsMain(true);
                    return warehouses.save(w);
                }))
                .orElseGet(() -> warehouses.save(WarehouseEntity.builder()
                        .code(DEFAULT_CODE)
                        .name("Bodega principal")
                        .active(true)
                        .isMain(true)
                        .build()))
                .getId();
    }
}
