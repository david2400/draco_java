package com.essenza.draco.modules.devolution.application.input.flow;

import com.essenza.draco.modules.devolution.application.dto.flow.ReturnableOrderDto;

public interface FindReturnableUseCase {
    ReturnableOrderDto forOrder(Long orderId, Long excludeDevolutionId);
}
