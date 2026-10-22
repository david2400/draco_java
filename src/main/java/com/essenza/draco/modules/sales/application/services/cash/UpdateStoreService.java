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
import com.essenza.draco.modules.sales.application.input.cash.UpdateStoreUseCase;

/** Un punto de venta con cajas abiertas no se desactiva. */
@Service
@Transactional
public class UpdateStoreService implements UpdateStoreUseCase {

    private final StoreRepository stores;
    private final CashSoporte soporte;

    public UpdateStoreService(StoreRepository stores, CashSoporte soporte) {
        this.stores = stores;
        this.soporte = soporte;
    }

    @Override
    public StoreDto update(Long id, SaveStoreDto input) {
        Store current = soporte.store(id);
        Store next = StoreFactory.from(id, input, input.active() == null ? current.active() : input.active());
        StoreFactory.check(stores, next, id);
        if (!next.active() && stores.countOpenRegisters(id) > 0) {
            throw new ConflictException("El punto de venta " + current.name() + " tiene cajas abiertas: ciérralas antes de desactivarlo.");
        }
        return soporte.storeDto(stores.save(next));
    }
}
