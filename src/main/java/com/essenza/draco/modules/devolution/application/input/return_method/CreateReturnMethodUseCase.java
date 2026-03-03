package com.essenza.draco.modules.devolution.application.input.return_method;

import com.essenza.draco.modules.devolution.domain.dto.return_method.CreateReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.ReturnMethodDto;

public interface CreateReturnMethodUseCase {
    ReturnMethodDto create(CreateReturnMethodDto input);
}
