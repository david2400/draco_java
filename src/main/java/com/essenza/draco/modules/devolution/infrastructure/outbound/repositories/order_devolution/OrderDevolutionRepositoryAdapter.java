package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.order_devolution;

import com.essenza.draco.modules.devolution.application.output.repository.OrderDevolutionRepository;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.CreateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.UpdateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.mappers.OrderDevolutionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderDevolutionRepositoryAdapter implements OrderDevolutionRepository {

    private final JpaOrderDevolutionRepository jpa;
    private final OrderDevolutionMapper mapper;

    public OrderDevolutionRepositoryAdapter(JpaOrderDevolutionRepository jpa, OrderDevolutionMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public OrderDevolutionDto create(CreateOrderDevolutionDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public OrderDevolutionDto update(Long id, UpdateOrderDevolutionDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OrderDevolution not found: " + id));
        mapper.updateEntityFromDto(input, entity);
        var updated = jpa.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }

    @Override
    public Optional<OrderDevolutionDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<OrderDevolutionDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}
