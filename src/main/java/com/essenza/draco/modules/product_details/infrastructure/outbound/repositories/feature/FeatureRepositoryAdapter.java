package com.essenza.draco.modules.product_details.infrastructure.outbound.repositories.feature;

import com.essenza.draco.modules.product_details.application.output.repository.FeatureRepository;
import com.essenza.draco.modules.product_details.domain.dto.feature.CreateFeatureDto;
import com.essenza.draco.modules.product_details.domain.dto.feature.FeatureDto;
import com.essenza.draco.modules.product_details.domain.dto.feature.UpdateFeatureDto;
import com.essenza.draco.modules.product_details.infrastructure.outbound.mappers.FeatureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FeatureRepositoryAdapter implements FeatureRepository {

    private final JpaFeatureRepository jpa;
    private final FeatureMapper mapper;

    public FeatureRepositoryAdapter(JpaFeatureRepository jpa, FeatureMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public FeatureDto create(CreateFeatureDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public FeatureDto update(Long id, UpdateFeatureDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + id));
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
    public Optional<FeatureDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<FeatureDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}
