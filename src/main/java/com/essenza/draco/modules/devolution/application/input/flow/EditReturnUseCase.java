package com.essenza.draco.modules.devolution.application.input.flow;

import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;

public interface EditReturnUseCase {
    OrderDevolutionDto edit(Long id, Long motiveId, String observation, Long returnMethodId, Long refundMethodId, String externalReference);
}
