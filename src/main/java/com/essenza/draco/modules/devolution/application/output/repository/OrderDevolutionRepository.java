package com.essenza.draco.modules.devolution.application.output.repository;

import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.CreateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.UpdateOrderDevolutionDto;

import java.util.List;
import java.util.Optional;

public interface OrderDevolutionRepository {

    OrderDevolutionDto create(CreateOrderDevolutionDto input);

    OrderDevolutionDto update(Long id, UpdateOrderDevolutionDto input);

    boolean deleteById(Long id);

    Optional<OrderDevolutionDto> findById(Long id);

    List<OrderDevolutionDto> findAll();

    org.springframework.data.domain.Page<OrderDevolutionDto> search(String query, String state, Long motiveDevolutionId, Long orderId, org.springframework.data.domain.Pageable pageable);
}
