package com.essenza.draco.modules.sales.application.dto.cash;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SaveStoreDto(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 200) String address,
        @Size(max = 80) String city,
        @Size(max = 40) String phone,
        @Email @Size(max = 120) String email,
        @NotNull @Schema(description = "Bodega de la que salen las ventas de mostrador") Long warehouseId,
        @Size(max = 255) String openingHours,
        @Size(max = 500) @Schema(description = "Encabezado del ticket (NIT, régimen, mensaje)") String ticketHeader,
        @Size(max = 500) @Schema(description = "Pie del ticket (políticas, agradecimiento)") String ticketFooter,
        Boolean active,
        @Schema(description = "País (catálogo del servicio parametros)") Long countryId,
        @Schema(description = "Departamento (catálogo del servicio parametros)") Long stateId,
        @Schema(description = "Ciudad (catálogo del servicio parametros); city guarda su nombre para el ticket") Long cityId) {
}
