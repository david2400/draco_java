package com.essenza.draco.modules.billing.application.input.tax_rate;

import com.essenza.draco.modules.billing.application.dto.tax_rate.SaveTaxRateDto;
import com.essenza.draco.modules.billing.application.dto.tax_rate.TaxRateDto;

public interface UpdateTaxRateUseCase {
    TaxRateDto update(Long id, SaveTaxRateDto input);
}
