package com.essenza.draco.modules.sales.application.input.order;

import org.springframework.data.domain.Pageable;

import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;

/** Búsqueda paginada con texto libre y filtros. */
public interface SearchOrdersUseCase {
    PageResponse<OrderDto> search(String query, String state, Pageable pageable);
}
