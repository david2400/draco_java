package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_detail;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_detail.DispatchDetailDto;

import java.util.Optional;

public interface FindDispatchDetailByIdUseCase {
    Optional<DispatchDetailDto> findById(Long id);
}
