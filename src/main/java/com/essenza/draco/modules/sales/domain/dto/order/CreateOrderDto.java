package com.essenza.draco.modules.sales.domain.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDto {
    private String complementaryOrder;
    @NotNull
    @PositiveOrZero
    private BigDecimal total;
    @NotBlank
    private String state;
}
