package com.essenza.draco.modules.catalog.application.dto.variant;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/** Estado del generador de variantes de un producto. */
public record VariantMatrixDto(
        Long productId,
        String productName,
        @Schema(description = "Precio y costo del producto (valores por defecto de las nuevas variantes)") BigDecimal basePrice,
        BigDecimal baseCost,
        @Schema(description = "Base sugerida para los códigos SKU de las variantes") String baseCode,
        Long templateId,
        String templateName,
        @Schema(description = "Ejes en uso: los guardados del producto y, si no hay, los de la plantilla") List<Axis> axes,
        @Schema(description = "Atributos de tipo opción que se pueden agregar como eje") List<AttributeRef> availableAttributes,
        List<Variant> variants) {

    @Schema(name = "VariantMatrixAxis")
    public record Axis(Long attributeId, String code, String name, boolean fromTemplate, boolean imageAxis, int position,
                       List<Option> options) {
    }

    @Schema(name = "VariantMatrixOption")
    public record Option(Long id, String value, int position,
                         @Schema(description = "Usado por alguna variante del producto") boolean selected,
                         @Schema(description = "Variantes del producto con este valor") long variantCount,
                         @Schema(description = "Imagen del valor en este producto") String imageUrl) {
    }

    @Schema(name = "VariantMatrixAttributeRef")
    public record AttributeRef(Long id, String code, String name, int optionsCount, boolean fromTemplate) {
    }

    @Schema(name = "VariantMatrixVariant")
    public record Variant(Long childId, Long skuId, String skuCode, String name, List<OptionRef> options,
                          BigDecimal price, BigDecimal costPrice, BigDecimal compareAtPrice, String barcode, String imageUrl,
                          boolean active, int stock,
                          @Schema(description = "Tuvo stock, reservas o ventas: al quitarla se desactiva en vez de borrarse") boolean hasHistory,
                          @Schema(description = "Le faltan valores de algún eje") boolean incomplete) {
    }

    @Schema(name = "VariantMatrixOptionRef")
    public record OptionRef(Long attributeId, Long optionId) {
    }
}
