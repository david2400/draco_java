package com.essenza.draco.modules.devolution.application.services;

import com.essenza.draco.modules.devolution.application.input.motive_devolution.*;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.CreateMotiveDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.MotiveDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.UpdateMotiveDevolutionDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.motive_devolution.MotiveDevolutionRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MotiveDevolutionServiceImpl implements CreateMotiveDevolutionUseCase,
        UpdateMotiveDevolutionUseCase,
        DeleteMotiveDevolutionUseCase,
        FindMotiveDevolutionByIdUseCase,
        FindMotiveDevolutionsUseCase {

    private final MotiveDevolutionRepositoryAdapter motiveDevolutionRepository;

    public MotiveDevolutionServiceImpl(MotiveDevolutionRepositoryAdapter motiveDevolutionRepository) {
        this.motiveDevolutionRepository = motiveDevolutionRepository;
    }


    @Override
    public MotiveDevolutionDto create(CreateMotiveDevolutionDto input) {
        return motiveDevolutionRepository.create(input);
    }

    @Override
    public MotiveDevolutionDto update(Long id, UpdateMotiveDevolutionDto input) {
        return motiveDevolutionRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return motiveDevolutionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MotiveDevolutionDto> findById(Long id) {
        return motiveDevolutionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MotiveDevolutionDto> findAll() {
        return motiveDevolutionRepository.findAll();
    }
}
