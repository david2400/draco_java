package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.return_method;

import com.essenza.draco.modules.devolution.application.output.repository.ReturnMethodRepository;
import com.essenza.draco.modules.devolution.domain.dto.return_method.CreateReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.ReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.UpdateReturnMethodDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.mappers.ReturnMethodMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReturnMethodRepositoryAdapter implements ReturnMethodRepository {

    private final JpaReturnMethodRepository jpa;
    private final ReturnMethodMapper mapper;

    public ReturnMethodRepositoryAdapter(JpaReturnMethodRepository jpa, ReturnMethodMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public ReturnMethodDto create(CreateReturnMethodDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public ReturnMethodDto update(Long id, UpdateReturnMethodDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ReturnMethod not found: " + id));
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
    public Optional<ReturnMethodDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<ReturnMethodDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}
