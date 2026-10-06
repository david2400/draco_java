package com.essenza.draco.modules.sales.application.output.repository;

import com.essenza.draco.modules.sales.application.dto.order.CreateOrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.UpdateOrderDto;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    OrderDto create(CreateOrderDto input);

    OrderDto update(Long id, UpdateOrderDto input);

    boolean deleteById(Long id);

    Optional<OrderDto> findById(Long id);

    List<OrderDto> findAll();

    org.springframework.data.domain.Page<OrderDto> search(String query, String state, org.springframework.data.domain.Pageable pageable);

    boolean hasDependents(Long id);
}
