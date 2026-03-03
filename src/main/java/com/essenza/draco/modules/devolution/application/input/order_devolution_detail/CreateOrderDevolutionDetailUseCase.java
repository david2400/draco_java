package com.essenza.draco.modules.devolution.application.input.order_devolution_detail;

import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.CreateOrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.OrderDevolutionDetailDto;

public interface CreateOrderDevolutionDetailUseCase {
    OrderDevolutionDetailDto create(CreateOrderDevolutionDetailDto input);
}
