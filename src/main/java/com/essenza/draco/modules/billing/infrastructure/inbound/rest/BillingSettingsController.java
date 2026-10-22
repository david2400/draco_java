package com.essenza.draco.modules.billing.infrastructure.inbound.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.essenza.draco.modules.billing.application.dto.settings.BillingSettingsDto;
import com.essenza.draco.modules.billing.application.dto.settings.SaveBillingSettingsDto;
import com.essenza.draco.modules.billing.application.input.settings.GetBillingSettingsUseCase;
import com.essenza.draco.modules.billing.application.input.settings.UpdateBillingSettingsUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/billing/settings")
@Tag(name = "Billing settings", description = "Datos del emisor y parámetros de facturación electrónica")
@RequiredArgsConstructor
public class BillingSettingsController {

    private final GetBillingSettingsUseCase getSettings;
    private final UpdateBillingSettingsUseCase updateSettings;

    @Operation(summary = "Get", description = "Incluye qué falta para poder facturar (missing) y si el proveedor está listo.")
    @GetMapping
    public BillingSettingsDto get() {
        return getSettings.get();
    }

    @Operation(summary = "Update", description = "Reemplaza los datos; software_pin vacío conserva el actual.")
    @PutMapping
    public BillingSettingsDto update(@RequestBody SaveBillingSettingsDto input) {
        return updateSettings.update(input);
    }
}
