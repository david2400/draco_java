package com.essenza.draco.modules.inventory.application.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.inventory.application.input.lookup.InventoryLookupUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.InventoryLookupQuery;
import com.essenza.draco.shared.common.lookup.LookupOption;
import com.essenza.draco.shared.common.lookup.LookupRequest;

@Service
@Transactional(readOnly = true)
public class InventoryLookupServiceImpl implements InventoryLookupUseCase {

    private final InventoryLookupQuery query;

    public InventoryLookupServiceImpl(InventoryLookupQuery query) {
        this.query = query;
    }

    @Override
    public List<LookupOption> suppliers(LookupRequest request) {
        return query.suppliers(request);
    }
}
