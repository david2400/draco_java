package com.essenza.draco.modules.devolution.application.input.flow;

import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.OrderDevolutionDetailDto;

public interface AddReturnLineUseCase {
    OrderDevolutionDetailDto add(Long devolutionId, Long productOrderId, int quantity, String observation);
}
