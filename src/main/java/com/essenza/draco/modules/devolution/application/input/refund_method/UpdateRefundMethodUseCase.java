package com.essenza.draco.modules.devolution.application.input.refund_method;

import com.essenza.draco.modules.devolution.domain.dto.refund_method.RefundMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.UpdateRefundMethodDto;

public interface UpdateRefundMethodUseCase {
    RefundMethodDto update(Long id, UpdateRefundMethodDto input);
}
