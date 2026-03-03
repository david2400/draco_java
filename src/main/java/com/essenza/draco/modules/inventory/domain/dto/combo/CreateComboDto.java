package com.essenza.draco.modules.inventory.domain.dto.combo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateComboDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private String imageUrl;
    private Boolean available;
    @NotNull
    @Positive
    private BigDecimal realPrice;
    @NotNull
    @Positive
    private BigDecimal price;
}
