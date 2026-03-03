package com.essenza.draco.modules.devolution.application.output.repository;

import com.essenza.draco.modules.devolution.domain.dto.return_method.CreateReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.ReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.UpdateReturnMethodDto;

import java.util.List;
import java.util.Optional;

public interface ReturnMethodRepository {

    ReturnMethodDto create(CreateReturnMethodDto input);

    ReturnMethodDto update(Long id, UpdateReturnMethodDto input);

    boolean deleteById(Long id);

    Optional<ReturnMethodDto> findById(Long id);

    List<ReturnMethodDto> findAll();
}
