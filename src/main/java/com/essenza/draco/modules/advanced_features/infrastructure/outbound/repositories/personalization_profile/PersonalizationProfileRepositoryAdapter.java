package com.essenza.draco.modules.advanced_features.infrastructure.outbound.repositories.personalization_profile;

import com.essenza.draco.modules.advanced_features.application.output.repository.PersonalizationProfileRepository;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.CreatePersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.PersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.UpdatePersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.infrastructure.outbound.mappers.PersonalizationProfileMapper;
import com.essenza.draco.modules.advanced_features.infrastructure.outbound.persistence.mysql.JpaPersonalizationProfileRepository;
import com.essenza.draco.modules.advanced_features.infrastructure.outbound.persistence.mysql.PersonalizationProfileEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PersonalizationProfileRepositoryAdapter implements PersonalizationProfileRepository {

    private final JpaPersonalizationProfileRepository jpaRepository;
    private final PersonalizationProfileMapper mapper;

    public PersonalizationProfileRepositoryAdapter(JpaPersonalizationProfileRepository jpaRepository,
                                                   PersonalizationProfileMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public PersonalizationProfileDto create(CreatePersonalizationProfileDto input) {
        PersonalizationProfileEntity entity = mapper.toEntity(input);
        PersonalizationProfileEntity saved = jpaRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public PersonalizationProfileDto update(Long id, UpdatePersonalizationProfileDto input) {
        PersonalizationProfileEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Personalization profile not found: " + id));
        mapper.updateEntityFromDto(input, entity);
        PersonalizationProfileEntity updated = jpaRepository.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpaRepository.existsById(id)) {
            return false;
        }
        jpaRepository.deleteById(id);
        return true;
    }

    @Override
    public Optional<PersonalizationProfileDto> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public List<PersonalizationProfileDto> findAll() {
        return mapper.toDtoList(jpaRepository.findAll());
    }
}
