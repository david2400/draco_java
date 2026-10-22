package com.essenza.draco.modules.catalog.application.dto.variant;

/** Resultado de guardar la matriz: cuántas variantes se crearon, actualizaron, desactivaron y borraron. */
public record SaveVariantMatrixResultDto(int created, int updated, int deactivated, int deleted, VariantMatrixDto matrix) {
}
