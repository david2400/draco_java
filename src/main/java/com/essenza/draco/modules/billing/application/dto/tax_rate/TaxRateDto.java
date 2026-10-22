package com.essenza.draco.modules.billing.application.dto.tax_rate;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxRateDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    @Schema(description = "IVA, INC o EXCLUDED")
    private String kind;
    @Schema(description = "Porcentaje (19.000 = 19 %)")
    private BigDecimal rate;
    private Boolean isDefault;
    private Boolean active;
    private Integer position;
    @Schema(description = "Productos que la usan explícitamente")
    private Long productsCount;
    @Schema(description = "Líneas de orden que la tienen congelada")
    private Long linesCount;
}
