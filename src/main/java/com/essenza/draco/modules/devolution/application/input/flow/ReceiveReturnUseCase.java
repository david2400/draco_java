package com.essenza.draco.modules.devolution.application.input.flow;

import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.dto.flow.ReceiveReturnDto;

public interface ReceiveReturnUseCase {
    OrderDevolutionDto receive(Long id, ReceiveReturnDto input);
}
