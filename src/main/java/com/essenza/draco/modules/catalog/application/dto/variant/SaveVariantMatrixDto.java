package com.essenza.draco.modules.catalog.application.dto.variant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/** Ejes, imágenes por valor y matriz completa de variantes (reemplaza el conjunto). */
public record SaveVariantMatrixDto(
        @NotEmpty @Size(max = 4) @Valid List<AxisInput> axes,
        @Size(max = 300) @Valid List<OptionImage> optionImages,
        @NotNull @Size(max = 300) @Valid List<Row> variants,
        @Schema(description = "Bodega del stock inicial de las variantes nuevas (vacío = principal)") Long initialWarehouseId) {

    @Schema(name = "SaveVariantMatrixAxis")
    public record AxisInput(@NotNull Long attributeId,
                            @Schema(description = "Este eje define la imagen de las variantes (p. ej. color)") Boolean imageAxis) {
    }

    @Schema(name = "SaveVariantMatrixOptionImage")
    public record OptionImage(@NotNull Long optionId, @Size(max = 500) String imageUrl) {
    }

    @Schema(name = "SaveVariantMatrixRow")
    public record Row(
            @Schema(description = "Variante existente (vacío = se busca por combinación o se crea)") Long childId,
            @NotEmpty @Valid List<VariantMatrixDto.OptionRef> options,
            @NotNull BigDecimal price,
            BigDecimal costPrice,
            BigDecimal compareAtPrice,
            @Size(max = 64) @Schema(description = "Vacío = se genera desde la base y los valores") String skuCode,
            @Size(max = 32) String barcode,
            @Size(max = 500) @Schema(description = "Vacío = imagen del valor del eje de imagen") String imageUrl,
            Boolean active,
            @Schema(description = "Solo para variantes nuevas") Integer initialStock) {
    }
}
