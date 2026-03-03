package com.essenza.draco.modules.inventory.domain.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WarehouseDto {
    private Long id;
    @NotBlank
    @Size(max = 150)
    private String name;
    @NotBlank
    @Size(max = 50)
    private String code;
    @Size(max = 255)
    private String address;
    private Boolean active = true;
}
