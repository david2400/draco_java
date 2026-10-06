package com.essenza.draco.modules.inventory.infrastructure.inbound.rest;

import com.essenza.draco.modules.inventory.application.dto.warehouse.CreateWarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.UpdateWarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.application.services.WarehouseServiceImpl;
import com.essenza.draco.shared.common.domain.dto.BulkIdsRequest;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseServiceImpl service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseDto create(@Valid @RequestBody CreateWarehouseDto dto) {
        return service.create(dto);
    }

    @GetMapping
    public Page<WarehouseDto> list(Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDto> getById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public WarehouseDto update(@PathVariable Long id, @Valid @RequestBody UpdateWarehouseDto dto) {
        return service.update(id, dto);
    }

    /** Eliminación lógica; 409 si la bodega todavía tiene stock. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    @PostMapping("/bulk-delete")
    public BulkOperationResult bulkDelete(@Valid @RequestBody BulkIdsRequest request) {
        return service.deleteAll(request.ids());
    }
}
