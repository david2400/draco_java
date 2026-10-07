package com.essenza.draco.modules.inventory.infrastructure.inbound.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.essenza.draco.modules.inventory.application.input.lookup.InventoryLookupUseCase;
import com.essenza.draco.shared.common.lookup.LookupOption;
import com.essenza.draco.shared.common.lookup.LookupRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Búsquedas ligeras del inventario (q, ids separados por coma, limit 1–50). */
@RestController
@Tag(name = "Inventory lookups")
public class InventoryLookupController {

    private final InventoryLookupUseCase lookup;

    public InventoryLookupController(InventoryLookupUseCase lookup) {
        this.lookup = lookup;
    }

    @Operation(summary = "Buscar proveedores", description = "Por nombre o correo; hint = correo")
    @GetMapping("/inventory/suppliers/lookup")
    public List<LookupOption> suppliers(@RequestParam(value = "q", required = false) String q,
                                        @RequestParam(value = "ids", required = false) String ids,
                                        @RequestParam(value = "limit", required = false) Integer limit) {
        return lookup.suppliers(LookupRequest.of(q, ids, limit));
    }
}
