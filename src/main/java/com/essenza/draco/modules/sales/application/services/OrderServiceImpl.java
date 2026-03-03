package com.essenza.draco.modules.sales.application.services;

import com.essenza.draco.modules.sales.application.input.order.*;
import com.essenza.draco.modules.sales.domain.dto.order.CreateOrderDto;
import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;
import com.essenza.draco.modules.sales.domain.dto.order.UpdateOrderDto;
import com.essenza.draco.modules.sales.infrastructure.outbound.repositories.order.OrderRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements CreateOrderUseCase,
        UpdateOrderUseCase,
        DeleteOrderUseCase,
        FindOrderByIdUseCase,
        FindOrdersUseCase {

    private final OrderRepositoryAdapter orderRepository;

    public OrderServiceImpl(OrderRepositoryAdapter orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Override
    public OrderDto create(CreateOrderDto input) {
        return orderRepository.create(input);
    }

    @Override
    public OrderDto update(Long id, UpdateOrderDto input) {
        return orderRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return orderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAll() {
        return orderRepository.findAll();
    }

}
