package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.order_devolution_detail;

import com.essenza.draco.modules.devolution.application.output.repository.OrderDevolutionDetailRepository;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.CreateOrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.OrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.UpdateOrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.mappers.OrderDevolutionDetailMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderDevolutionDetailRepositoryAdapter implements OrderDevolutionDetailRepository {

    private final JpaOrderDevolutionDetailRepository jpa;
    private final OrderDevolutionDetailMapper mapper;

    public OrderDevolutionDetailRepositoryAdapter(JpaOrderDevolutionDetailRepository jpa, OrderDevolutionDetailMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public OrderDevolutionDetailDto create(CreateOrderDevolutionDetailDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public OrderDevolutionDetailDto update(Long id, UpdateOrderDevolutionDetailDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OrderDevolutionDetail not found: " + id));
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
    public Optional<OrderDevolutionDetailDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<OrderDevolutionDetailDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}
