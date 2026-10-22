package com.essenza.draco.modules.billing.application.input.tax_rate;

import java.util.List;

import com.essenza.draco.modules.billing.application.dto.tax_rate.TaxRateDto;

public interface FindTaxRatesUseCase {
    List<TaxRateDto> findAll(boolean includeInactive);

    TaxRateDto findById(Long id);
}
