package com.essenza.draco.modules.devolution.application.output.repository;

import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.OrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.CreateOrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.UpdateOrderDevolutionDetailDto;

import java.util.List;
import java.util.Optional;

public interface OrderDevolutionDetailRepository {

    OrderDevolutionDetailDto create(CreateOrderDevolutionDetailDto input);

    OrderDevolutionDetailDto update(Long id, UpdateOrderDevolutionDetailDto input);

    boolean deleteById(Long id);

    Optional<OrderDevolutionDetailDto> findById(Long id);

    List<OrderDevolutionDetailDto> findAll();
}
