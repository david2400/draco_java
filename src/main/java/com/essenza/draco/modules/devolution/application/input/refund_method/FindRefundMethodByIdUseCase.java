package com.essenza.draco.modules.devolution.application.input.refund_method;

import com.essenza.draco.modules.devolution.domain.dto.refund_method.RefundMethodDto;

import java.util.Optional;

public interface FindRefundMethodByIdUseCase {
    Optional<RefundMethodDto> findById(Long id);
}
