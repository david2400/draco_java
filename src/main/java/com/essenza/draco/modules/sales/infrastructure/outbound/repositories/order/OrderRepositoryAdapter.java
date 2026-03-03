package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.order;

import com.essenza.draco.modules.sales.application.output.repository.OrderRepository;
import com.essenza.draco.modules.sales.domain.dto.order.CreateOrderDto;
import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;
import com.essenza.draco.modules.sales.domain.dto.order.UpdateOrderDto;
import com.essenza.draco.modules.sales.infrastructure.outbound.mappers.OrderMapper;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.OrderEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    private final JpaOrderRepository jpa;
    private final OrderMapper mapper;

    public OrderRepositoryAdapter(JpaOrderRepository jpa, OrderMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }


    @Override
    public OrderDto create(CreateOrderDto order) {
        OrderEntity saved = jpa.save(mapper.toEntity(order));
        return mapper.toDto(saved);
    }

    @Override
    public OrderDto update(Long id, UpdateOrderDto input) {
        OrderEntity saved = jpa.save(mapper.toEntity(input));
        return mapper.toDto(saved);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }

    @Override
    public Optional<OrderDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<OrderDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}
