package com.essenza.draco.modules.catalog.infrastructure.inbound.rest;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.essenza.draco.modules.catalog.application.dto.attribute.ProductAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveProductAttributesDto;
import com.essenza.draco.modules.catalog.application.input.attribute.ProductAttributesUseCase;

@RestController
@RequestMapping("/catalog/products/{productId}/attributes")
@Tag(name = "Product attributes", description = "Ficha técnica y ejes de variante de un producto")
public class ProductAttributesController {

    private final ProductAttributesUseCase productAttributes;

    public ProductAttributesController(ProductAttributesUseCase productAttributes) {
        this.productAttributes = productAttributes;
    }

    @Operation(summary = "Get product attributes",
            description = "Plantilla, atributos disponibles, valores, ejes por variante y lo que falta para publicar.")
    @GetMapping
    public ProductAttributesDto get(@PathVariable Long productId) {
        return productAttributes.get(productId);
    }

    @Operation(summary = "Save product attributes",
            description = "Reemplaza plantilla, ficha y ejes de variante. Si el producto está publicado (ACTIVE) "
                    + "exige los obligatorios y todos los ejes en cada variante. 400 si una regla no se cumple.")
    @PutMapping
    public ProductAttributesDto save(@PathVariable Long productId, @Valid @RequestBody SaveProductAttributesDto input) {
        return productAttributes.save(productId, input);
    }
}
