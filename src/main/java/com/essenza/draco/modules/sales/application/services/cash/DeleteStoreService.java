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
import com.essenza.draco.modules.sales.application.input.cash.DeleteStoreUseCase;

@Service
@Transactional
public class DeleteStoreService implements DeleteStoreUseCase {

    private final StoreRepository stores;
    private final CashSoporte soporte;

    public DeleteStoreService(StoreRepository stores, CashSoporte soporte) {
        this.stores = stores;
        this.soporte = soporte;
    }

    @Override
    public void delete(Long id) {
        Store store = soporte.store(id);
        if (stores.countRegisters(id) > 0) {
            throw new ConflictException("El punto de venta " + store.name() + " tiene cajas: muévelas o desactívalo en lugar de eliminarlo.");
        }
        stores.delete(id);
    }
}
