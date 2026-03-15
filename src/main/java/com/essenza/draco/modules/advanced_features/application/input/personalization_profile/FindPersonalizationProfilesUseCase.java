package com.essenza.draco.modules.advanced_features.application.input.personalization_profile;

import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.PersonalizationProfileDto;

import java.util.List;

public interface FindPersonalizationProfilesUseCase {
    List<PersonalizationProfileDto> findAll();
}
