package com.essenza.draco.modules.billing.application.services.tax_rate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.tax_rate.SaveTaxRateDto;
import com.essenza.draco.modules.billing.application.dto.tax_rate.TaxRateDto;
import com.essenza.draco.modules.billing.application.input.tax_rate.CreateTaxRateUseCase;
import com.essenza.draco.modules.billing.application.output.repository.TaxRateRepository;
import com.essenza.draco.modules.billing.domain.model.TaxRate;

@Service
@Transactional
public class CreateTaxRateService implements CreateTaxRateUseCase {

    private final TaxRateSoporte soporte;
    private final TaxRateRepository rates;

    public CreateTaxRateService(TaxRateSoporte soporte, TaxRateRepository rates) {
        this.soporte = soporte;
        this.rates = rates;
    }

    @Override
    public TaxRateDto create(SaveTaxRateDto input) {
        TaxRate rate = soporte.toDomain(null, input, null);
        if (!rate.isDefault() && rates.findDefault().isEmpty() && rate.active()) {
            // La primera tarifa activa queda por defecto.
            rate = new TaxRate(null, rate.code(), rate.name(), rate.description(), rate.kind(), rate.rate(), true, true,
                    rate.position());
        }
        return soporte.toDto(soporte.saveKeepingSingleDefault(rate));
    }
}
