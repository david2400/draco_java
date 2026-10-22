package com.essenza.draco.modules.devolution.application.input.flow;

import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.dto.flow.ReturnRequestDto;

public interface RequestReturnUseCase {
    OrderDevolutionDto request(ReturnRequestDto input);
}
