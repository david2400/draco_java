package com.essenza.draco.modules.devolution.infrastructure.inbound.rest;

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
import com.essenza.draco.modules.devolution.application.input.order_devolution.BulkDeleteOrderDevolutionsUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution.SearchOrderDevolutionsUseCase;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.OrderDevolutionDto;

/**
 * Endpoints de administración (búsqueda paginada y lote) sobre la misma ruta
 * base que el CRUD existente.
 */
@RestController
@RequestMapping("/devolution/order_devolutions")
@Tag(name = "Order devolutions")
public class OrderDevolutionSearchController {

    private static final Set<String> SORTABLE = Set.of("id", "state", "orderId", "totalRefundAmount", "createdAt", "updatedAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("id"));

    private final SearchOrderDevolutionsUseCase searchOrderDevolutions;
    private final BulkDeleteOrderDevolutionsUseCase bulkDeleteOrderDevolutions;

    public OrderDevolutionSearchController(SearchOrderDevolutionsUseCase searchOrderDevolutions, BulkDeleteOrderDevolutionsUseCase bulkDeleteOrderDevolutions) {
        this.searchOrderDevolutions = searchOrderDevolutions;
        this.bulkDeleteOrderDevolutions = bulkDeleteOrderDevolutions;
    }

    @Operation(summary = "Search", description = "Búsqueda paginada: q, filtros, page, size, sort=campo,asc|desc")
    @GetMapping("/search")
    public PageResponse<OrderDevolutionDto> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "motiveDevolutionId", required = false) Long motiveDevolutionId,
            @RequestParam(value = "orderId", required = false) Long orderId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        return searchOrderDevolutions.search(q, state, motiveDevolutionId, orderId, PageableFactory.of(page, size, sort, SORTABLE, DEFAULT_SORT));
    }

    @Operation(summary = "Bulk delete", description = "Elimina varios registros; los fallos se informan en 'failed'.")
    @PostMapping("/bulk-delete")
    public BulkOperationResult bulkDelete(@Valid @RequestBody BulkIdsRequest request) {
        return bulkDeleteOrderDevolutions.deleteAll(request.ids());
    }
}
