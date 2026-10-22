package com.essenza.draco.modules.devolution.application.input.flow;

import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;

public interface GetReturnUseCase {
    OrderDevolutionDto get(Long id);
}
