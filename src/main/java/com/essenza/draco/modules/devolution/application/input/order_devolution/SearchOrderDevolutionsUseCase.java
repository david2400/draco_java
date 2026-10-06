package com.essenza.draco.modules.devolution.application.input.order_devolution;

import org.springframework.data.domain.Pageable;

import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;

/** Búsqueda paginada con texto libre y filtros. */
public interface SearchOrderDevolutionsUseCase {
    PageResponse<OrderDevolutionDto> search(String query, String state, Long motiveDevolutionId, Long orderId, Pageable pageable);
}
