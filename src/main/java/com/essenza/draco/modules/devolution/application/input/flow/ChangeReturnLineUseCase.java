package com.essenza.draco.modules.devolution.application.input.flow;

import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.OrderDevolutionDetailDto;

public interface ChangeReturnLineUseCase {
    OrderDevolutionDetailDto change(Long detailId, int quantity, String observation);
}
