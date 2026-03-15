package com.essenza.draco.modules.advanced_features.application.input.personalization_profile;

import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.CreatePersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.PersonalizationProfileDto;

public interface CreatePersonalizationProfileUseCase {
    PersonalizationProfileDto create(CreatePersonalizationProfileDto input);
}
