package com.essenza.draco.modules.catalog.application.input.attribute;

import java.util.List;
import java.util.Optional;

import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveAttributeDto;

/** Alta, edición, consulta y borrado de atributos. */
public interface ManageAttributesUseCase {

    List<AttributeDto> findAll();

    Optional<AttributeDto> findById(Long id);

    AttributeDto create(SaveAttributeDto input);

    AttributeDto update(Long id, SaveAttributeDto input);

    void delete(Long id);
}
