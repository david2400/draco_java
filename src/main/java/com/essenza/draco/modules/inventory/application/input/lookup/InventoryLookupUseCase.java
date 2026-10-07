package com.essenza.draco.modules.inventory.application.input.lookup;

import java.util.List;

import com.essenza.draco.shared.common.lookup.LookupOption;
import com.essenza.draco.shared.common.lookup.LookupRequest;

public interface InventoryLookupUseCase {

    List<LookupOption> suppliers(LookupRequest request);
}
