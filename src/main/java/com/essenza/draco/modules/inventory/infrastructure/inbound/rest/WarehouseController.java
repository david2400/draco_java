package com.essenza.draco.modules.inventory.infrastructure.inbound.rest;

import com.essenza.draco.modules.inventory.domain.dto.warehouse.CreateWarehouseDto;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.UpdateWarehouseDto;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.application.services.WarehouseServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouses")
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

    @PutMapping("/{id}")
    public WarehouseDto update(@PathVariable Long id, @Valid @RequestBody UpdateWarehouseDto dto) {
        return service.update(id, dto);
    }
}
