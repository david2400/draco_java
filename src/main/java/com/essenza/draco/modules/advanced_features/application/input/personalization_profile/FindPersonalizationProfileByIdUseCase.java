package com.essenza.draco.modules.advanced_features.application.input.personalization_profile;

import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.PersonalizationProfileDto;

import java.util.Optional;

public interface FindPersonalizationProfileByIdUseCase {
    Optional<PersonalizationProfileDto> findById(Long id);
}
