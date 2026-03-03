package com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderDevolutionDetailDto extends CreateOrderDevolutionDetailDto {
    @NotNull
    @Positive
    private Long id;
}
