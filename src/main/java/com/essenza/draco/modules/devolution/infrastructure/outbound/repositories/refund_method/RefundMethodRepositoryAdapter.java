package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.refund_method;

import com.essenza.draco.modules.devolution.application.output.repository.RefundMethodRepository;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.CreateRefundMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.RefundMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.UpdateRefundMethodDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.mappers.RefundMethodMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RefundMethodRepositoryAdapter implements RefundMethodRepository {

    private final JpaRefundMethodRepository jpa;
    private final RefundMethodMapper mapper;

    public RefundMethodRepositoryAdapter(JpaRefundMethodRepository jpa, RefundMethodMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public RefundMethodDto create(CreateRefundMethodDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public RefundMethodDto update(Long id, UpdateRefundMethodDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RefundMethod not found: " + id));
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
    public Optional<RefundMethodDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<RefundMethodDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}
