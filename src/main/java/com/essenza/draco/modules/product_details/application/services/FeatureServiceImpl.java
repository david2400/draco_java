package com.essenza.draco.modules.product_details.application.services;

import com.essenza.draco.modules.product_details.application.input.feature.*;
import com.essenza.draco.modules.product_details.domain.dto.feature.CreateFeatureDto;
import com.essenza.draco.modules.product_details.domain.dto.feature.FeatureDto;
import com.essenza.draco.modules.product_details.domain.dto.feature.UpdateFeatureDto;
import com.essenza.draco.modules.product_details.infrastructure.outbound.repositories.feature.FeatureRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class FeatureServiceImpl implements CreateFeatureUseCase, UpdateFeatureUseCase, DeleteFeatureUseCase, FindFeaturesUseCase, FindFeatureByIdUseCase {

    private final FeatureRepositoryAdapter featureRepository;

//    public FeatureServiceImpl(FeatureRepositoryAdapter featureRepository) {
//        this.featureRepository = featureRepository;
//    }

    @Override
    public FeatureDto create(CreateFeatureDto input) {
        return featureRepository.create(input);
    }

    @Override
    public FeatureDto update(Long id, UpdateFeatureDto input) {
        return featureRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
//        if (!featureRepository.existsById(id)) return false;
//        featureRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeatureDto> findById(Long id) {
        return featureRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeatureDto> findAll() {
        return featureRepository.findAll();
    }
}
