package com.essenza.draco.modules.advanced_features.application.services.personalization_profile;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.advanced_features.application.dto.personalization_profile.CreatePersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.application.dto.personalization_profile.PersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.CreatePersonalizationProfileUseCase;
import com.essenza.draco.modules.advanced_features.application.output.repository.PersonalizationProfileRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CreatePersonalizationProfileService implements CreatePersonalizationProfileUseCase {

    private final PersonalizationProfileRepository repository;

    @Override
    public PersonalizationProfileDto create(CreatePersonalizationProfileDto input) {
        if (input.getLastAnalysisAt() == null) {
            input.setLastAnalysisAt(Instant.now());
        }
        return repository.create(input);
    }
}
