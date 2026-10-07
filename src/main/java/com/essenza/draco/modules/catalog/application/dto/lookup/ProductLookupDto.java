package com.essenza.draco.modules.catalog.application.dto.lookup;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

/** Producto en formato ligero para selectores con búsqueda. */
public record ProductLookupDto(
        Long id,
        String name,
        String slug,
        @Schema(allowableValues = {"DRAFT", "ACTIVE", "INACTIVE", "ARCHIVED"}) String status,
        @Schema(allowableValues = {"SIMPLE", "VARIANT", "COMBO"}) String productType,
        BigDecimal unitPrice,
        String imageUrl) {
}
