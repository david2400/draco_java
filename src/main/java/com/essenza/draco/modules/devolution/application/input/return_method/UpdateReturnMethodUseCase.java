package com.essenza.draco.modules.devolution.application.input.return_method;

import com.essenza.draco.modules.devolution.domain.dto.return_method.ReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.UpdateReturnMethodDto;

public interface UpdateReturnMethodUseCase {
    ReturnMethodDto update(Long id, UpdateReturnMethodDto input);
}
