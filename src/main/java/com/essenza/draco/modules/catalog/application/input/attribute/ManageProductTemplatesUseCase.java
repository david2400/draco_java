package com.essenza.draco.modules.catalog.application.input.attribute;

import java.util.List;
import java.util.Optional;

import com.essenza.draco.modules.catalog.application.dto.attribute.ProductTemplateDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveProductTemplateDto;

/** Alta, edición, consulta y borrado de plantillas de producto. */
public interface ManageProductTemplatesUseCase {

    List<ProductTemplateDto> findAll();

    Optional<ProductTemplateDto> findById(Long id);

    ProductTemplateDto create(SaveProductTemplateDto input);

    ProductTemplateDto update(Long id, SaveProductTemplateDto input);

    void delete(Long id);
}
