package com.essenza.draco.modules.inventory.application.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/** Ficha del proveedor. En el listado {@code contacts} viene vacío y se resume el contacto principal. */
public record SupplierDto(
        Long id,
        String code,
        String docType,
        String docNumber,
        @Schema(description = "Dígito de verificación (NIT)") String dv,
        @Schema(description = "Documento para mostrar: 900123456-8") String document,
        String personType,
        String name,
        String tradeName,
        @Schema(description = "Nombre comercial o, si no hay, razón social") String displayName,
        String taxRegime,
        boolean greatContributor,
        boolean selfRetainer,
        String email,
        String phone,
        String website,
        String address,
        Long countryId,
        Long stateId,
        Long cityId,
        String city,
        String status,
        String blockReason,
        int paymentTermDays,
        BigDecimal creditLimit,
        String currency,
        Integer leadTimeDays,
        BigDecimal minOrderAmount,
        BigDecimal discountPercent,
        String bankName,
        String bankAccountType,
        String bankAccountNumber,
        @Schema(description = "Cuenta enmascarada: ****1234") String bankAccountMasked,
        String bankAccountHolder,
        String bankHolderDocument,
        String notes,
        List<SupplierContactDto> contacts,
        String primaryContactName,
        String primaryContactPhone,
        String primaryContactEmail,
        @Schema(description = "SKU activos que surte") long productsCount,
        @Schema(description = "Productos del catálogo que lo tienen como proveedor principal (solo en el detalle)") long catalogProducts) {
}
