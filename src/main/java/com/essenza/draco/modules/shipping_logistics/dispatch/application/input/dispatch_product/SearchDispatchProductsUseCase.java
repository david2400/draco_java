package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product;

import org.springframework.data.domain.Pageable;

import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.DispatchProductDto;

/** Búsqueda paginada con texto libre y filtros. */
public interface SearchDispatchProductsUseCase {
    PageResponse<DispatchProductDto> search(String query, Long orderId, String cityDestination, Pageable pageable);
}
