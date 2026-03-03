package com.essenza.draco.modules.devolution.application.input.order_devolution_detail;

import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.OrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.UpdateOrderDevolutionDetailDto;

public interface UpdateOrderDevolutionDetailUseCase {
    OrderDevolutionDetailDto update(Long id, UpdateOrderDevolutionDetailDto input);
}
