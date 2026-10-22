package com.essenza.draco.modules.inventory.infrastructure.inbound.rest;

import com.essenza.draco.modules.inventory.application.dto.supplier.ChangeSupplierStatusDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SaveSupplierDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SaveSupplierProductDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierProductDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierStatsDto;
import com.essenza.draco.modules.inventory.application.input.supplier.BulkDeleteSuppliersUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.ChangeSupplierStatusUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.CreateSupplierUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.DeleteSupplierProductUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.DeleteSupplierUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.FindSupplierByIdUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.FindSuppliersUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.ListSupplierProductsUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.SaveSupplierProductUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.SearchSuppliersUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.SupplierStatsUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.UpdateSupplierUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierFilter;
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
@RequestMapping("/inventory/suppliers")
@Tag(name = "Suppliers", description = "Proveedores: ficha fiscal y comercial, contactos, estado y productos que surten")
@RequiredArgsConstructor
public class SupplierController {

    private static final Set<String> SORTABLE = Set.of("id", "code", "name", "tradeName", "status", "city",
            "paymentTermDays", "createdAt", "updatedAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("name"));

    private final FindSuppliersUseCase findSuppliers;
    private final SearchSuppliersUseCase searchSuppliers;
    private final SupplierStatsUseCase supplierStats;
    private final FindSupplierByIdUseCase findSupplier;
    private final CreateSupplierUseCase createSupplier;
    private final UpdateSupplierUseCase updateSupplier;
    private final ChangeSupplierStatusUseCase changeStatus;
    private final DeleteSupplierUseCase deleteSupplier;
    private final BulkDeleteSuppliersUseCase bulkDeleteSuppliers;
    private final ListSupplierProductsUseCase listProducts;
    private final SaveSupplierProductUseCase saveProduct;
    private final DeleteSupplierProductUseCase deleteProduct;

    @Operation(summary = "List all", description = "Todos los proveedores sin contactos (selects y exportación). Para tablas usar /search.")
    @GetMapping
    public List<SupplierDto> list() {
        return findSuppliers.findAll();
    }

    @Operation(summary = "Search", description = "Paginado. q busca en razón social, nombre comercial, código, documento, correo y ciudad; "
            + "filtros status (ACTIVE | BLOCKED | INACTIVE) y cityId.")
    @GetMapping("/search")
    public PageResponse<SupplierDto> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "cityId", required = false) Long cityId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        return PageResponse.from(searchSuppliers.search(new SupplierFilter(q, status, cityId),
                PageableFactory.of(page, size, sort, SORTABLE, DEFAULT_SORT)));
    }

    @Operation(summary = "Stats", description = "Totales por estado, sin documento y con crédito.")
    @GetMapping("/stats")
    public SupplierStatsDto stats() {
        return supplierStats.stats();
    }

    @Operation(summary = "Get", description = "Ficha completa con contactos y conteo de productos.")
    @GetMapping("/{id}")
    public SupplierDto get(@PathVariable Long id) {
        return findSupplier.findById(id);
    }

    @Operation(summary = "Create", description = "Documento obligatorio; código y documento únicos (409). Código vacío = PROV_<n>.")
    @PostMapping
    public ResponseEntity<SupplierDto> create(@Valid @RequestBody SaveSupplierDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createSupplier.create(input));
    }

    @Operation(summary = "Update", description = "Reemplaza la ficha y sus contactos. Sin status se conserva el actual.")
    @PutMapping("/{id}")
    public SupplierDto update(@PathVariable Long id, @Valid @RequestBody SaveSupplierDto input) {
        return updateSupplier.update(id, input);
    }

    @Operation(summary = "Change status", description = "Activar, bloquear (motivo obligatorio) o desactivar.")
    @PostMapping("/{id}/status")
    public SupplierDto changeStatus(@PathVariable Long id, @Valid @RequestBody ChangeSupplierStatusDto input) {
        return changeStatus.change(id, input);
    }

    @Operation(summary = "Delete", description = "409 si es proveedor principal de productos del catálogo.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteSupplier.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bulk delete", description = "Cada id por separado; los que no se pueden borrar se informan con su motivo.")
    @PostMapping("/bulk-delete")
    public BulkOperationResult bulkDelete(@Valid @RequestBody BulkIdsRequest request) {
        return bulkDeleteSuppliers.deleteAll(request.ids());
    }

    @Operation(summary = "Products", description = "SKU que surte el proveedor con costo, margen y entrega.")
    @GetMapping("/{id}/products")
    public List<SupplierProductDto> products(@PathVariable Long id) {
        return listProducts.list(id);
    }

    @Operation(summary = "Add product", description = "409 si ya surte el SKU. preferred desmarca a los demás proveedores del SKU.")
    @PostMapping("/{id}/products")
    public ResponseEntity<SupplierProductDto> addProduct(@PathVariable Long id, @Valid @RequestBody SaveSupplierProductDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saveProduct.save(id, null, input));
    }

    @Operation(summary = "Update product")
    @PutMapping("/{id}/products/{productId}")
    public SupplierProductDto updateProduct(@PathVariable Long id, @PathVariable Long productId,
                                            @Valid @RequestBody SaveSupplierProductDto input) {
        return saveProduct.save(id, productId, input);
    }

    @Operation(summary = "Remove product")
    @DeleteMapping("/{id}/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, @PathVariable Long productId) {
        deleteProduct.delete(id, productId);
        return ResponseEntity.noContent().build();
    }
}
