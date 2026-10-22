package com.essenza.draco.modules.sales.infrastructure.inbound.rest;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.essenza.draco.modules.sales.application.dto.cash.SaveStoreDto;
import com.essenza.draco.modules.sales.application.dto.cash.StoreDto;
import com.essenza.draco.modules.sales.application.input.cash.CreateStoreUseCase;
import com.essenza.draco.modules.sales.application.input.cash.DeleteStoreUseCase;
import com.essenza.draco.modules.sales.application.input.cash.FindStoresUseCase;
import com.essenza.draco.modules.sales.application.input.cash.UpdateStoreUseCase;

@RestController
@RequestMapping("/sales/stores")
@Tag(name = "Stores", description = "Puntos de venta (tiendas o sedes) con su bodega y ticket")
public class StoreController {

    private final FindStoresUseCase findStores;
    private final CreateStoreUseCase createStore;
    private final UpdateStoreUseCase updateStore;
    private final DeleteStoreUseCase deleteStore;

    public StoreController(FindStoresUseCase findStores, CreateStoreUseCase createStore, UpdateStoreUseCase updateStore,
                           DeleteStoreUseCase deleteStore) {
        this.findStores = findStores;
        this.createStore = createStore;
        this.updateStore = updateStore;
        this.deleteStore = deleteStore;
    }

    @Operation(summary = "List", description = "Puntos de venta con número de cajas y cajas abiertas.")
    @GetMapping
    public List<StoreDto> list(@RequestParam(value = "includeInactive", defaultValue = "true") boolean includeInactive) {
        return findStores.findAll(includeInactive);
    }

    @Operation(summary = "Create")
    @PostMapping
    public ResponseEntity<StoreDto> create(@Valid @RequestBody SaveStoreDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createStore.create(input));
    }

    @Operation(summary = "Update", description = "409 al desactivar con cajas abiertas.")
    @PutMapping("/{id}")
    public StoreDto update(@PathVariable Long id, @Valid @RequestBody SaveStoreDto input) {
        return updateStore.update(id, input);
    }

    @Operation(summary = "Delete", description = "409 si tiene cajas.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteStore.delete(id);
        return ResponseEntity.noContent().build();
    }
}
