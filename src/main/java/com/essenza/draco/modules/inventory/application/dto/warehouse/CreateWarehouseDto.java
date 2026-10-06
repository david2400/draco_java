package com.essenza.draco.modules.inventory.application.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWarehouseDto {
    @NotBlank
    @Size(max = 150)
    private String name;
    @NotBlank
    @Size(max = 50)
    private String code;
    @Size(max = 255)
    private String address;
    private Boolean active = true;

    /** País (id del catálogo de georreferenciación del servicio parametros). */
    private Long countryId;
    /** Departamento (id del catálogo de georreferenciación del servicio parametros). */
    private Long stateId;
    /** Ciudad (id del catálogo de georreferenciación del servicio parametros). */
    private Long cityId;
}
