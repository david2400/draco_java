package com.essenza.draco.modules.inventory.application.output.repository;

import java.util.List;

import com.essenza.draco.shared.common.lookup.LookupOption;
import com.essenza.draco.shared.common.lookup.LookupRequest;

/** Consultas ligeras del inventario para selectores con búsqueda. */
public interface InventoryLookupQuery {

    List<LookupOption> suppliers(LookupRequest request);
}
