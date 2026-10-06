package com.essenza.draco.modules.sales.application.dto.order;

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
    /** Calculado por el backend (suma de las líneas); se ignora si se envía. */
    @PositiveOrZero
    private BigDecimal total;
    /** Las órdenes se crean PENDING; los cambios de estado van por POST /sales/orders/{id}/status. */
    private String state;
}
