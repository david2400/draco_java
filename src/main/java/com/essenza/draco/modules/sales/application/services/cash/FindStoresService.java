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
import com.essenza.draco.modules.sales.application.input.cash.FindStoresUseCase;

@Service
@Transactional(readOnly = true)
public class FindStoresService implements FindStoresUseCase {

    private final StoreRepository stores;
    private final CashSoporte soporte;

    public FindStoresService(StoreRepository stores, CashSoporte soporte) {
        this.stores = stores;
        this.soporte = soporte;
    }

    @Override
    public List<StoreDto> findAll(boolean includeInactive) {
        return stores.findAll().stream().filter(s -> includeInactive || s.active())
                .sorted(Comparator.comparing(Store::code)).map(soporte::storeDto).toList();
    }
}
