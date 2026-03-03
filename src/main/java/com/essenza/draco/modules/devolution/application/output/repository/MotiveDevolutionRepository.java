package com.essenza.draco.modules.devolution.application.output.repository;

import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.MotiveDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.CreateMotiveDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.UpdateMotiveDevolutionDto;

import java.util.List;
import java.util.Optional;

public interface MotiveDevolutionRepository {

    MotiveDevolutionDto create(CreateMotiveDevolutionDto input);

    MotiveDevolutionDto update(Long id, UpdateMotiveDevolutionDto input);

    boolean deleteById(Long id);

    Optional<MotiveDevolutionDto> findById(Long id);

    List<MotiveDevolutionDto> findAll();

    Optional<MotiveDevolutionDto> findByName(String name);
}
