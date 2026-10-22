package com.essenza.draco.modules.billing.application.dto.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueInvoiceDto {
    @NotNull
    private Long orderId;
    @Schema(description = "Vacío = consumidor final")
    private BuyerDto buyer;
}
