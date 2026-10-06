package com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.inbound.rest;

import java.util.Set;

import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.essenza.draco.shared.common.domain.dto.BulkIdsRequest;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.shared.common.web.PageableFactory;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product.BulkDeleteDispatchProductsUseCase;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product.SearchDispatchProductsUseCase;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.dto.dispatch_product.DispatchProductDto;

/**
 * Endpoints de administración (búsqueda paginada y lote) sobre la misma ruta
 * base que el CRUD existente.
 */
@RestController
@RequestMapping("/dispatch/dispatch_products")
@Tag(name = "Dispatch products")
public class DispatchProductSearchController {

    private static final Set<String> SORTABLE = Set.of("id", "guideNumber", "estimatedDeliveryDate", "realDeliveryDate", "orderId", "cityDestination", "createdAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("id"));

    private final SearchDispatchProductsUseCase searchDispatchProducts;
    private final BulkDeleteDispatchProductsUseCase bulkDeleteDispatchProducts;

    public DispatchProductSearchController(SearchDispatchProductsUseCase searchDispatchProducts, BulkDeleteDispatchProductsUseCase bulkDeleteDispatchProducts) {
        this.searchDispatchProducts = searchDispatchProducts;
        this.bulkDeleteDispatchProducts = bulkDeleteDispatchProducts;
    }

    @Operation(summary = "Search", description = "Búsqueda paginada: q, filtros, page, size, sort=campo,asc|desc")
    @GetMapping("/search")
    public PageResponse<DispatchProductDto> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "orderId", required = false) Long orderId,
            @RequestParam(value = "cityDestination", required = false) String cityDestination,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        return searchDispatchProducts.search(q, orderId, cityDestination, PageableFactory.of(page, size, sort, SORTABLE, DEFAULT_SORT));
    }

    @Operation(summary = "Bulk delete", description = "Elimina varios registros; los fallos se informan en 'failed'.")
    @PostMapping("/bulk-delete")
    public BulkOperationResult bulkDelete(@Valid @RequestBody BulkIdsRequest request) {
        return bulkDeleteDispatchProducts.deleteAll(request.ids());
    }
}
