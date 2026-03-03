package com.essenza.draco.modules.sales.domain.dto.payment_type;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentTypeDto {
    @NotBlank
    private String name;
}
