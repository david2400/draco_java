package com.essenza.draco.modules.advanced_features.application.services.personalization_profile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.DeletePersonalizationProfileUseCase;
import com.essenza.draco.modules.advanced_features.application.output.repository.PersonalizationProfileRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeletePersonalizationProfileService implements DeletePersonalizationProfileUseCase {

    private final PersonalizationProfileRepository repository;

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }
}
