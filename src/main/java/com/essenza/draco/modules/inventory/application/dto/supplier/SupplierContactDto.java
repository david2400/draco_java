package com.essenza.draco.modules.inventory.application.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Persona de contacto (entrada y salida). */
@Schema(name = "SupplierContactDto")
public record SupplierContactDto(
        Long id,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 80) String position,
        @Schema(description = "VENTAS, CARTERA, LOGISTICA, GERENCIA u OTRO") String area,
        @Size(max = 40) String phone,
        @Email @Size(max = 120) String email,
        @Schema(description = "Contacto principal (si ninguno lo es, el primero)") Boolean primary) {
}
