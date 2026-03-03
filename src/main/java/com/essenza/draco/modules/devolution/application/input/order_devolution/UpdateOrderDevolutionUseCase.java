package com.essenza.draco.modules.devolution.application.input.order_devolution;

import com.essenza.draco.modules.devolution.domain.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.UpdateOrderDevolutionDto;

public interface UpdateOrderDevolutionUseCase {
    OrderDevolutionDto update(Long id, UpdateOrderDevolutionDto input);
}
