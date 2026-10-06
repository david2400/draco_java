package com.essenza.draco.modules.devolution.application.input.order_devolution;

import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;

import java.util.Optional;

public interface FindOrderDevolutionByIdUseCase {
    Optional<OrderDevolutionDto> findById(Long id);
}
