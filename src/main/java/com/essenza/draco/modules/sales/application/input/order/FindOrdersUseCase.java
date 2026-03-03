package com.essenza.draco.modules.sales.application.input.order;

import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;
import java.util.List;

public interface FindOrdersUseCase {
    List<OrderDto> findAll();
}
