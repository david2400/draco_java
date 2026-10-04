package com.essenza.draco.modules.sales.infrastructure.inbound.rest;

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
import com.essenza.draco.modules.sales.application.input.order.BulkDeleteOrdersUseCase;
import com.essenza.draco.modules.sales.application.input.order.SearchOrdersUseCase;
import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;

/**
 * Endpoints de administración (búsqueda paginada y lote) sobre la misma ruta
 * base que el CRUD existente.
 */
@RestController
@RequestMapping("/sales/orders")
@Tag(name = "Orders")
public class OrderSearchController {

    private static final Set<String> SORTABLE = Set.of("id", "total", "state", "createdAt", "updatedAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("id"));

    private final SearchOrdersUseCase searchOrders;
    private final BulkDeleteOrdersUseCase bulkDeleteOrders;

    public OrderSearchController(SearchOrdersUseCase searchOrders, BulkDeleteOrdersUseCase bulkDeleteOrders) {
        this.searchOrders = searchOrders;
        this.bulkDeleteOrders = bulkDeleteOrders;
    }

    @Operation(summary = "Search", description = "Búsqueda paginada: q, filtros, page, size, sort=campo,asc|desc")
    @GetMapping("/search")
    public PageResponse<OrderDto> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        return searchOrders.search(q, state, PageableFactory.of(page, size, sort, SORTABLE, DEFAULT_SORT));
    }

    @Operation(summary = "Bulk delete", description = "Elimina varios registros; los fallos se informan en 'failed'.")
    @PostMapping("/bulk-delete")
    public BulkOperationResult bulkDelete(@Valid @RequestBody BulkIdsRequest request) {
        return bulkDeleteOrders.deleteAll(request.ids());
    }
}
