package com.essenza.draco.modules.catalog.application.input.variant;

import com.essenza.draco.modules.catalog.application.dto.variant.CreateAxisOptionDto;
import com.essenza.draco.modules.catalog.application.dto.variant.SaveVariantMatrixDto;
import com.essenza.draco.modules.catalog.application.dto.variant.SaveVariantMatrixResultDto;
import com.essenza.draco.modules.catalog.application.dto.variant.VariantMatrixDto;

public interface VariantMatrixUseCase {

    VariantMatrixDto get(Long productId);

    SaveVariantMatrixResultDto save(Long productId, SaveVariantMatrixDto input);

    VariantMatrixDto.Option createOption(Long productId, CreateAxisOptionDto input);
}
