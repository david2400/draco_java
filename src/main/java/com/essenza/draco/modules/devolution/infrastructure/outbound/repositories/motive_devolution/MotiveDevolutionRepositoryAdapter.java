package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.motive_devolution;

import com.essenza.draco.modules.devolution.application.output.repository.MotiveDevolutionRepository;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.CreateMotiveDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.MotiveDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.UpdateMotiveDevolutionDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.mappers.MotiveDevolutionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MotiveDevolutionRepositoryAdapter implements MotiveDevolutionRepository {

    private final JpaMotiveDevolutionRepository jpa;
    private final MotiveDevolutionMapper mapper;

    public MotiveDevolutionRepositoryAdapter(JpaMotiveDevolutionRepository jpa, MotiveDevolutionMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public MotiveDevolutionDto create(CreateMotiveDevolutionDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public MotiveDevolutionDto update(Long id, UpdateMotiveDevolutionDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MotiveDevolution not found: " + id));
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
    public Optional<MotiveDevolutionDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<MotiveDevolutionDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public Optional<MotiveDevolutionDto> findByName(String name) {
        return jpa.findByName(name).map(mapper::toDto);
    }
}
