package com.essenza.draco.modules.billing.application.services.tax_rate;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.output.repository.TaxRateRepository;
import com.essenza.draco.modules.billing.domain.model.TaxRate;
import com.essenza.draco.shared.common.billing.TaxRates;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Implementación del contrato {@link TaxRates} para catálogo y ventas. */
@Service
@Transactional(readOnly = true)
public class TaxRatesService implements TaxRates {

    /** Sin tarifa por defecto configurada: la venta no causa impuesto (no se bloquea la operación). */
    static final TaxRateView NONE = new TaxRateView(null, "SIN_TARIFA", "Sin tarifa", "EXCLUDED", BigDecimal.ZERO);

    private final TaxRateRepository rates;

    public TaxRatesService(TaxRateRepository rates) {
        this.rates = rates;
    }

    @Override
    public TaxRateView effective(Long taxRateId) {
        if (taxRateId == null) {
            return rates.findDefault().map(TaxRatesService::view).orElse(NONE);
        }
        return rates.findById(taxRateId).map(TaxRatesService::view)
                .orElseThrow(() -> new NotFoundException("No se encontró la tarifa de impuesto " + taxRateId + "."));
    }

    @Override
    public boolean isAssignable(Long taxRateId) {
        return taxRateId != null && rates.findById(taxRateId).map(TaxRate::active).orElse(false);
    }

    private static TaxRateView view(TaxRate rate) {
        return new TaxRateView(rate.id(), rate.code(), rate.name(), rate.kind().name(), rate.rate());
    }
}
