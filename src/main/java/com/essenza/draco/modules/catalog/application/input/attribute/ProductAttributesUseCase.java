package com.essenza.draco.modules.catalog.application.input.attribute;

import com.essenza.draco.modules.catalog.application.dto.attribute.ProductAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveProductAttributesDto;

/** Ficha técnica y ejes de variante de un producto. */
public interface ProductAttributesUseCase {

    ProductAttributesDto get(Long productId);

    ProductAttributesDto save(Long productId, SaveProductAttributesDto input);
}
