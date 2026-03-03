package com.essenza.draco.modules.devolution.domain.dto.refund_method;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotBlank;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRefundMethodDto {
    @NotBlank
    private String name;

    private String description;

    private String policy;

    private Boolean active;
}
