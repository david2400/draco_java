package com.essenza.draco.modules.advanced_features.application.services.personalization_profile;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.advanced_features.application.dto.personalization_profile.PersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.application.dto.personalization_profile.UpdatePersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.UpdatePersonalizationProfileUseCase;
import com.essenza.draco.modules.advanced_features.application.output.repository.PersonalizationProfileRepository;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdatePersonalizationProfileService implements UpdatePersonalizationProfileUseCase {

    private final PersonalizationProfileRepository repository;

    @Override
    public PersonalizationProfileDto update(Long id, UpdatePersonalizationProfileDto input) {
        repository.findById(id).orElseThrow(() -> new NotFoundException("Personalization profile not found: " + id));
        if (input.getLastAnalysisAt() == null) {
            input.setLastAnalysisAt(Instant.now());
        }
        return repository.update(id, input);
    }
}
