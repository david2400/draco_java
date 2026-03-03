package com.essenza.draco.modules.devolution.application.input.return_method;

import com.essenza.draco.modules.devolution.domain.dto.return_method.ReturnMethodDto;

import java.util.List;

public interface FindReturnMethodsUseCase {
    List<ReturnMethodDto> findAll();
}
