package com.essenza.draco.modules.advanced_features.application.services.personalization_profile;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.advanced_features.application.dto.personalization_profile.PersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.FindPersonalizationProfileByIdUseCase;
import com.essenza.draco.modules.advanced_features.application.output.repository.PersonalizationProfileRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindPersonalizationProfileByIdService implements FindPersonalizationProfileByIdUseCase {

    private final PersonalizationProfileRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonalizationProfileDto> findById(Long id) {
        return repository.findById(id);
    }
}
