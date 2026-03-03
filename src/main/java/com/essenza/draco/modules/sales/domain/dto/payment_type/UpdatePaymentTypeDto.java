package com.essenza.draco.modules.sales.domain.dto.payment_type;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentTypeDto extends CreatePaymentTypeDto {
    @NotNull
    @Positive
    private Long id;
}
