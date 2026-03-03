package com.essenza.draco.modules.devolution.application.input.return_method;

import com.essenza.draco.modules.devolution.domain.dto.return_method.ReturnMethodDto;

import java.util.Optional;

public interface FindReturnMethodByIdUseCase {
    Optional<ReturnMethodDto> findById(Long id);
}
