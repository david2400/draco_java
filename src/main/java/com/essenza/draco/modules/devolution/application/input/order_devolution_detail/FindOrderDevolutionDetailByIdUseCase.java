package com.essenza.draco.modules.devolution.application.input.order_devolution_detail;

import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.OrderDevolutionDetailDto;

import java.util.Optional;

public interface FindOrderDevolutionDetailByIdUseCase {
    Optional<OrderDevolutionDetailDto> findById(Long id);
}
