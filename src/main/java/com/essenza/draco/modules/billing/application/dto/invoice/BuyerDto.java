package com.essenza.draco.modules.billing.application.dto.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerDto {
    @Schema(description = "CC, NIT, CE, PP, TI, PEP o NIT_EXT")
    private String docType;
    private String docNumber;
    @Schema(description = "Dígito de verificación (solo NIT, calculado)")
    private String dv;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String city;
}
