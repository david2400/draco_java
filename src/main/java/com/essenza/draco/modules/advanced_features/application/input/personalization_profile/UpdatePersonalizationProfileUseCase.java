package com.essenza.draco.modules.advanced_features.application.input.personalization_profile;

import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.PersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.UpdatePersonalizationProfileDto;

public interface UpdatePersonalizationProfileUseCase {
    PersonalizationProfileDto update(Long id, UpdatePersonalizationProfileDto input);
}
