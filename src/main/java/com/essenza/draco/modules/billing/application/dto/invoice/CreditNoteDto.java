package com.essenza.draco.modules.billing.application.dto.invoice;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditNoteDto {
    @NotBlank
    private String reason;
}
