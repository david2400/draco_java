package com.essenza.draco.modules.sales.application.input.cash;

import com.essenza.draco.modules.sales.application.dto.cash.SaveStoreDto;
import com.essenza.draco.modules.sales.application.dto.cash.StoreDto;

public interface FindStoresUseCase {
    java.util.List<StoreDto> findAll(boolean includeInactive);
}
