package com.essenza.draco.modules.catalog.application.services.product_child;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product_child.CreateProductChildDto;
import com.essenza.draco.modules.catalog.application.output.repository.UnitReferences;

/** Código compartido por los casos de uso de product_child (antes en ProductChildServiceImpl). */
@Component
@RequiredArgsConstructor
class ProductChildSoporte {

    private final UnitReferences unitReferences;

    /** Contenido neto: valor y unidad juntos, valor > 0 y unidad existente. */
    void checkNetContent(CreateProductChildDto input) {
        if (input.getNetContent() == null && input.getNetContentUnitId() == null) {
            return;
        }
        if (input.getNetContent() == null || input.getNetContentUnitId() == null) {
            throw new IllegalArgumentException("El contenido neto necesita valor y unidad.");
        }
        if (input.getNetContent().signum() <= 0) {
            throw new IllegalArgumentException("El contenido neto debe ser mayor que 0.");
        }
        if (!unitReferences.exists(input.getNetContentUnitId())) {
            throw new IllegalArgumentException("La unidad del contenido neto no existe: " + input.getNetContentUnitId());
        }
    }
}
