package com.essenza.draco.modules.devolution.application.input.refund_method;

import com.essenza.draco.modules.devolution.domain.dto.refund_method.CreateRefundMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.RefundMethodDto;

public interface CreateRefundMethodUseCase {
    RefundMethodDto create(CreateRefundMethodDto input);
}
