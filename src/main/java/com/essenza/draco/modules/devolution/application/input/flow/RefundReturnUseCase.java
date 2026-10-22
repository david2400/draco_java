package com.essenza.draco.modules.devolution.application.input.flow;

import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.dto.flow.RefundReturnDto;

public interface RefundReturnUseCase {
    OrderDevolutionDto refund(Long id, RefundReturnDto input);
}
