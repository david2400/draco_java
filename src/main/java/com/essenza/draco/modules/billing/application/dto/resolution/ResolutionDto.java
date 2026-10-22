package com.essenza.draco.modules.billing.application.dto.resolution;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolutionDto {
    private Long id;
    @Schema(description = "INVOICE o CREDIT_NOTE")
    private String kind;
    private String prefix;
    private String resolutionNumber;
    private LocalDate resolutionDate;
    private String technicalKey;
    private Long fromNumber;
    private Long toNumber;
    private Long nextNumber;
    private LocalDate validFrom;
    private LocalDate validTo;
    @Schema(description = "TEST o PRODUCTION")
    private String environment;
    private Boolean active;
    @Schema(description = "Números disponibles")
    private Long remaining;
    @Schema(description = "Días para el vencimiento (negativo si venció)")
    private Long daysToExpire;
    @Schema(description = "Ya se emitieron documentos con esta numeración")
    private Boolean used;
    @Schema(description = "Avisos: EXPIRED, EXPIRING, EXHAUSTED, LOW")
    private java.util.List<String> alerts;
}
