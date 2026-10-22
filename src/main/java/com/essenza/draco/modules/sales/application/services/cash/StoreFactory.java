package com.essenza.draco.modules.sales.application.services.cash;

import com.essenza.draco.modules.sales.application.dto.cash.SaveStoreDto;
import com.essenza.draco.modules.sales.application.output.repository.StoreRepository;
import com.essenza.draco.modules.sales.domain.model.cash.CashRuleViolation;
import com.essenza.draco.modules.sales.domain.model.cash.Store;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Arma y valida un punto de venta desde el formulario. */
final class StoreFactory {

    private StoreFactory() {
    }

    static Store from(Long id, SaveStoreDto input, boolean active) {
        return new Store(id, input.code(), input.name(), input.address(), input.city(), input.phone(), input.email(),
                input.warehouseId(), input.openingHours(), input.ticketHeader(), input.ticketFooter(), active,
                input.countryId(), input.stateId(), input.cityId());
    }

    static void check(StoreRepository stores, Store store, Long excludeId) {
        if (stores.existsCode(store.code(), excludeId)) {
            throw new ConflictException("Ya existe un punto de venta con el código " + store.code() + ".");
        }
        if (store.warehouseId() == null) {
            throw new CashRuleViolation("Elige la bodega de la que salen las ventas del punto de venta.");
        }
        if (!stores.warehouseExists(store.warehouseId())) {
            throw new NotFoundException("No se encontró la bodega " + store.warehouseId() + ".");
        }
    }
}
