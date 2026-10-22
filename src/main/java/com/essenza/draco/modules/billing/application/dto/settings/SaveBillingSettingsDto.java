package com.essenza.draco.modules.billing.application.dto.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveBillingSettingsDto {
    private String nit;
    private String legalName;
    private String tradeName;
    private String taxRegime;
    private String responsibilities;
    private String address;
    private String city;
    private String department;
    private String email;
    private String phone;
    private String environment;
    private String provider;
    private String softwareId;
    /** Vacío = conservar el actual. */
    private String softwarePin;
    private Boolean autoInvoice;
    private String invoiceFooter;
}
