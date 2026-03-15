package com.essenza.draco.modules.advanced_features.application.output.repository;

import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.CreatePersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.PersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.UpdatePersonalizationProfileDto;

import java.util.List;
import java.util.Optional;

public interface PersonalizationProfileRepository {

    PersonalizationProfileDto create(CreatePersonalizationProfileDto input);

    PersonalizationProfileDto update(Long id, UpdatePersonalizationProfileDto input);

    boolean deleteById(Long id);

    Optional<PersonalizationProfileDto> findById(Long id);

    List<PersonalizationProfileDto> findAll();
}
