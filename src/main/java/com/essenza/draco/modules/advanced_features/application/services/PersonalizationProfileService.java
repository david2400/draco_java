package com.essenza.draco.modules.advanced_features.application.services;

import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.CreatePersonalizationProfileUseCase;
import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.DeletePersonalizationProfileUseCase;
import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.FindPersonalizationProfileByIdUseCase;
import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.FindPersonalizationProfilesUseCase;
import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.UpdatePersonalizationProfileUseCase;
import com.essenza.draco.modules.advanced_features.application.output.repository.PersonalizationProfileRepository;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.CreatePersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.PersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.UpdatePersonalizationProfileDto;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonalizationProfileService implements CreatePersonalizationProfileUseCase,
        UpdatePersonalizationProfileUseCase,
        DeletePersonalizationProfileUseCase,
        FindPersonalizationProfileByIdUseCase,
        FindPersonalizationProfilesUseCase {

    private final PersonalizationProfileRepository repository;

    public PersonalizationProfileService(PersonalizationProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public PersonalizationProfileDto create(CreatePersonalizationProfileDto input) {
        if (input.getLastAnalysisAt() == null) {
            input.setLastAnalysisAt(Instant.now());
        }
        return repository.create(input);
    }

    @Override
    public PersonalizationProfileDto update(Long id, UpdatePersonalizationProfileDto input) {
        repository.findById(id).orElseThrow(() -> new NotFoundException("Personalization profile not found: " + id));
        if (input.getLastAnalysisAt() == null) {
            input.setLastAnalysisAt(Instant.now());
        }
        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonalizationProfileDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalizationProfileDto> findAll() {
        return repository.findAll();
    }
}
