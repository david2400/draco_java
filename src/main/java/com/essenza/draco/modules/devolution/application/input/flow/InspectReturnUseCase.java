package com.essenza.draco.modules.devolution.application.input.flow;

import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.dto.flow.InspectReturnDto;

public interface InspectReturnUseCase {
    OrderDevolutionDto inspect(Long id, InspectReturnDto input);
}
