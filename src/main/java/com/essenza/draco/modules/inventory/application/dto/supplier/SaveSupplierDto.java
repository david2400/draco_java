package com.essenza.draco.modules.inventory.application.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/** Alta o edición de la ficha del proveedor. */
public record SaveSupplierDto(
        @Size(max = 30) @Schema(description = "Código interno; vacío = PROV_<n>") String code,
        @NotBlank @Schema(description = "NIT, CC, CE, PP, PEP o NIT_EXT") String docType,
        @NotBlank @Size(max = 20) @Schema(description = "Número sin dígito de verificación (se calcula para NIT)") String docNumber,
        @Schema(description = "NATURAL o JURIDICA (por defecto según el documento)") String personType,
        @NotBlank @Size(max = 255) @Schema(description = "Razón social o nombre completo") String name,
        @Size(max = 160) String tradeName,
        @Schema(description = "RESPONSABLE_IVA, NO_RESPONSABLE_IVA o SIMPLE") String taxRegime,
        Boolean greatContributor,
        Boolean selfRetainer,
        @Email @Size(max = 255) String email,
        @Size(max = 40) String phone,
        @Size(max = 160) String website,
        @Size(max = 255) String address,
        @Schema(description = "País (catálogo del servicio parametros)") Long countryId,
        @Schema(description = "Departamento (catálogo del servicio parametros)") Long stateId,
        @Schema(description = "Ciudad (catálogo del servicio parametros)") Long cityId,
        @Size(max = 80) @Schema(description = "Nombre de la ciudad") String city,
        @Schema(description = "ACTIVE, BLOCKED o INACTIVE (por defecto ACTIVE al crear; se conserva al editar)") String status,
        @Size(max = 255) @Schema(description = "Obligatorio si el estado es BLOCKED") String blockReason,
        @Schema(description = "Plazo de pago en días; 0 = contado") Integer paymentTermDays,
        BigDecimal creditLimit,
        @Size(max = 3) @Schema(description = "ISO 4217, por defecto COP") String currency,
        @Schema(description = "Días de entrega habituales") Integer leadTimeDays,
        BigDecimal minOrderAmount,
        @Schema(description = "Descuento comercial acordado (%)") BigDecimal discountPercent,
        @Size(max = 80) String bankName,
        @Schema(description = "AHORROS o CORRIENTE") String bankAccountType,
        @Size(max = 30) String bankAccountNumber,
        @Size(max = 160) String bankAccountHolder,
        @Size(max = 20) String bankHolderDocument,
        @Size(max = 1000) String notes,
        @Valid @Size(max = 20) List<SupplierContactDto> contacts) {
}
