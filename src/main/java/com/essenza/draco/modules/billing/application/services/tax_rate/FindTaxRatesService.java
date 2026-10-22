package com.essenza.draco.modules.billing.application.services.tax_rate;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.tax_rate.TaxRateDto;
import com.essenza.draco.modules.billing.application.input.tax_rate.FindTaxRatesUseCase;
import com.essenza.draco.modules.billing.application.output.repository.TaxRateRepository;
import com.essenza.draco.modules.billing.domain.model.TaxRate;

@Service
@Transactional(readOnly = true)
public class FindTaxRatesService implements FindTaxRatesUseCase {

    private final TaxRateSoporte soporte;
    private final TaxRateRepository rates;

    public FindTaxRatesService(TaxRateSoporte soporte, TaxRateRepository rates) {
        this.soporte = soporte;
        this.rates = rates;
    }

    @Override
    public List<TaxRateDto> findAll(boolean includeInactive) {
        return rates.findAll().stream()
                .filter(rate -> includeInactive || rate.active())
                .sorted(Comparator.comparingInt(TaxRate::position).thenComparing(TaxRate::code))
                .map(soporte::toDto)
                .toList();
    }

    @Override
    public TaxRateDto findById(Long id) {
        return soporte.toDto(soporte.get(id));
    }
}
