package com.essenza.draco.modules.billing.application.dto.tax_rate;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveTaxRateDto {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    @Schema(description = "IVA, INC o EXCLUDED")
    private String kind;
    private BigDecimal rate;
    private Boolean isDefault;
    private Boolean active;
    private Integer position;
}
