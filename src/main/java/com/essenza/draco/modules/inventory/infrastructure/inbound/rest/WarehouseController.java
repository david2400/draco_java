package com.essenza.draco.modules.inventory.infrastructure.inbound.rest;

import com.essenza.draco.modules.inventory.application.dto.warehouse.SaveLocationDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.SaveWarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.UpdateMinThresholdDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseLinksDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseLocationDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseMovementDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseStatsDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseStockDto;
import com.essenza.draco.modules.inventory.application.input.warehouse.ChangeWarehouseStatusUseCase;
import com.essenza.draco.modules.inventory.application.input.warehouse.CreateWarehouseUseCase;
import com.essenza.draco.modules.inventory.application.input.warehouse.DeleteWarehouseUseCase;
import com.essenza.draco.modules.inventory.application.input.warehouse.FindWarehousesUseCase;
import com.essenza.draco.modules.inventory.application.input.warehouse.SetMainWarehouseUseCase;
import com.essenza.draco.modules.inventory.application.input.warehouse.UpdateWarehouseUseCase;
import com.essenza.draco.modules.inventory.application.input.warehouse.WarehouseLocationsUseCase;
import com.essenza.draco.modules.inventory.application.input.warehouse.WarehouseStockUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseFilter;
import com.essenza.draco.shared.common.domain.dto.BulkIdsRequest;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.shared.common.web.PageableFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/inventory/warehouses")
@Tag(name = "Warehouses", description = "Bodegas: tipo, operación, responsable, capacidad, ubicaciones, existencias y mínimos")
@RequiredArgsConstructor
public class WarehouseController {

    private static final Set<String> SORTABLE = Set.of("id", "code", "name", "warehouseType", "city", "active",
            "fulfillmentPriority", "createdAt", "updatedAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("isMain"), Sort.Order.asc("name"));
    private static final Set<String> STOCK_SORTABLE = Set.of("skuCode", "productName", "onHand", "reserved", "available",
            "minThreshold", "stockValue");

    private final FindWarehousesUseCase findWarehouses;
    private final CreateWarehouseUseCase createWarehouse;
    private final UpdateWarehouseUseCase updateWarehouse;
    private final ChangeWarehouseStatusUseCase changeStatus;
    private final SetMainWarehouseUseCase setMain;
    private final DeleteWarehouseUseCase deleteWarehouse;
    private final WarehouseStockUseCase warehouseStock;
    private final WarehouseLocationsUseCase locations;

    @Operation(summary = "List", description = "Página de bodegas (compatibilidad: page, size, sort). Para tablas usar /search.")
    @GetMapping
    public PageResponse<WarehouseDto> list(@RequestParam(value = "page", required = false) Integer page,
                                           @RequestParam(value = "size", required = false) Integer size,
                                           @RequestParam(value = "sort", required = false) String sort) {
        return PageResponse.from(findWarehouses.search(new WarehouseFilter(null, null, null, null),
                PageableFactory.of(page, size, sort, SORTABLE, DEFAULT_SORT)));
    }

    @Operation(summary = "Search", description = "q busca en nombre, código, ciudad, dirección y responsable; "
            + "filtros warehouseType, active (true | false) y cityId.")
    @GetMapping("/search")
    public PageResponse<WarehouseDto> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "warehouseType", required = false) String type,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "cityId", required = false) Long cityId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        return PageResponse.from(findWarehouses.search(new WarehouseFilter(q, type, active, cityId),
                PageableFactory.of(page, size, sort, SORTABLE, DEFAULT_SORT)));
    }

    @Operation(summary = "Stats", description = "Totales, unidades, valor al costo, SKU bajo mínimo y bodega principal.")
    @GetMapping("/stats")
    public WarehouseStatsDto stats() {
        return findWarehouses.stats();
    }

    @Operation(summary = "Get", description = "Bodega con resumen de existencias, ocupación, ubicaciones y tiendas.")
    @GetMapping("/{id}")
    public WarehouseDto get(@PathVariable Long id) {
        return findWarehouses.findById(id);
    }

    @Operation(summary = "Create", description = "Código vacío = BOD_<n>; 409 si el código existe. La primera bodega queda como principal.")
    @PostMapping
    public ResponseEntity<WarehouseDto> create(@Valid @RequestBody SaveWarehouseDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createWarehouse.create(input));
    }

    @Operation(summary = "Update", description = "Conserva la marca de principal; desactivar sigue las reglas de /status.")
    @PutMapping("/{id}")
    public WarehouseDto update(@PathVariable Long id, @Valid @RequestBody SaveWarehouseDto input) {
        return updateWarehouse.update(id, input);
    }

    @Operation(summary = "Activate / deactivate", description = "409 al desactivar la principal, con reservas o con tiendas activas.")
    @PostMapping("/{id}/status")
    public WarehouseDto status(@PathVariable Long id, @RequestParam("active") boolean active) {
        return changeStatus.setActive(id, active);
    }

    @Operation(summary = "Set main", description = "Marca la bodega como principal (activa, tipo almacén o tienda) y desmarca la anterior.")
    @PostMapping("/{id}/main")
    public WarehouseDto main(@PathVariable Long id) {
        return setMain.setMain(id);
    }

    @Operation(summary = "Delete", description = "Borrado lógico; 409 si es la principal, tiene stock o la usan tiendas o cajas.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteWarehouse.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bulk delete")
    @PostMapping("/bulk-delete")
    public BulkOperationResult bulkDelete(@Valid @RequestBody BulkIdsRequest request) {
        return deleteWarehouse.deleteAll(request.ids());
    }

    @Operation(summary = "Stock", description = "Existencias por SKU valorizadas al costo; belowMin = solo bajo mínimo.")
    @GetMapping("/{id}/stock")
    public PageResponse<WarehouseStockDto> stock(@PathVariable Long id,
                                                 @RequestParam(value = "q", required = false) String q,
                                                 @RequestParam(value = "belowMin", defaultValue = "false") boolean belowMin,
                                                 @RequestParam(value = "page", required = false) Integer page,
                                                 @RequestParam(value = "size", required = false) Integer size,
                                                 @RequestParam(value = "sort", required = false) String sort) {
        return PageResponse.from(warehouseStock.stock(id, q, belowMin,
                PageableFactory.of(page, size, sort, STOCK_SORTABLE, Sort.unsorted())));
    }

    @Operation(summary = "Min threshold", description = "Stock mínimo del SKU en la bodega (crea el registro si no existe).")
    @PutMapping("/{id}/stock/{skuId}/min")
    public WarehouseStockDto updateMin(@PathVariable Long id, @PathVariable Long skuId,
                                       @Valid @RequestBody UpdateMinThresholdDto input) {
        return warehouseStock.updateMin(id, skuId, input);
    }

    @Operation(summary = "Recent movements")
    @GetMapping("/{id}/movements")
    public List<WarehouseMovementDto> movements(@PathVariable Long id,
                                                @RequestParam(value = "limit", defaultValue = "15") int limit) {
        return warehouseStock.movements(id, limit);
    }

    @Operation(summary = "Linked stores and registers")
    @GetMapping("/{id}/links")
    public WarehouseLinksDto links(@PathVariable Long id) {
        return warehouseStock.links(id);
    }

    @Operation(summary = "Locations")
    @GetMapping("/{id}/locations")
    public List<WarehouseLocationDto> locations(@PathVariable Long id) {
        return locations.list(id);
    }

    @Operation(summary = "Add location", description = "409 si el código ya existe en la bodega.")
    @PostMapping("/{id}/locations")
    public ResponseEntity<WarehouseLocationDto> addLocation(@PathVariable Long id, @Valid @RequestBody SaveLocationDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locations.save(id, null, input));
    }

    @Operation(summary = "Update location")
    @PutMapping("/{id}/locations/{locationId}")
    public WarehouseLocationDto updateLocation(@PathVariable Long id, @PathVariable Long locationId,
                                               @Valid @RequestBody SaveLocationDto input) {
        return locations.save(id, locationId, input);
    }

    @Operation(summary = "Remove location")
    @DeleteMapping("/{id}/locations/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id, @PathVariable Long locationId) {
        locations.delete(id, locationId);
        return ResponseEntity.noContent().build();
    }
}
