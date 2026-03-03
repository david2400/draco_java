package com.essenza.draco.modules.devolution.application.input.order_devolution_detail;

import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.OrderDevolutionDetailDto;

import java.util.List;

public interface FindOrderDevolutionDetailsUseCase {
    List<OrderDevolutionDetailDto> findAll();
}
