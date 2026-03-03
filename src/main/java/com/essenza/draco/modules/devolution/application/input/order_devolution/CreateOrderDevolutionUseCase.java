package com.essenza.draco.modules.devolution.application.input.order_devolution;

import com.essenza.draco.modules.devolution.domain.dto.order_devolution.CreateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.OrderDevolutionDto;

public interface CreateOrderDevolutionUseCase {
    OrderDevolutionDto create(CreateOrderDevolutionDto input);
}
