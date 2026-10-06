package com.essenza.draco.modules.devolution.application.input.refund_method;

import com.essenza.draco.modules.devolution.application.dto.refund_method.RefundMethodDto;

import java.util.List;

public interface FindRefundMethodsUseCase {
    List<RefundMethodDto> findAll();
}
