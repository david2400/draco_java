package com.essenza.draco.modules.sales.application.services.cash;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.cash.SaveStoreDto;
import com.essenza.draco.modules.sales.application.dto.cash.StoreDto;
import com.essenza.draco.modules.sales.application.output.repository.StoreRepository;
import com.essenza.draco.modules.sales.domain.model.cash.Store;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import com.essenza.draco.modules.sales.application.input.cash.CreateStoreUseCase;

/** Nuevo punto de venta: código único y bodega existente. */
@Service
@Transactional
public class CreateStoreService implements CreateStoreUseCase {

    private final StoreRepository stores;
    private final CashSoporte soporte;

    public CreateStoreService(StoreRepository stores, CashSoporte soporte) {
        this.stores = stores;
        this.soporte = soporte;
    }

    @Override
    public StoreDto create(SaveStoreDto input) {
        Store store = StoreFactory.from(null, input, input.active() == null || input.active());
        StoreFactory.check(stores, store, null);
        return soporte.storeDto(stores.save(store));
    }
}
