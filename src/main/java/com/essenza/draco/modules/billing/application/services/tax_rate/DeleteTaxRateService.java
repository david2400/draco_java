package com.essenza.draco.modules.billing.application.services.tax_rate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.input.tax_rate.DeleteTaxRateUseCase;
import com.essenza.draco.modules.billing.application.output.repository.TaxRateRepository;
import com.essenza.draco.modules.billing.domain.model.TaxRate;
import com.essenza.draco.shared.exceptions.ConflictException;

@Service
@Transactional
public class DeleteTaxRateService implements DeleteTaxRateUseCase {

    private final TaxRateSoporte soporte;
    private final TaxRateRepository rates;

    public DeleteTaxRateService(TaxRateSoporte soporte, TaxRateRepository rates) {
        this.soporte = soporte;
        this.rates = rates;
    }

    @Override
    public void delete(Long id) {
        TaxRate rate = soporte.get(id);
        if (rate.isDefault()) {
            throw new ConflictException("No se puede eliminar la tarifa por defecto.");
        }
        long products = rates.productsUsing(id);
        long lines = rates.linesUsing(id);
        if (products > 0 || lines > 0) {
            throw new ConflictException("\"" + rate.name() + "\" está en uso (" + products + " productos, " + lines
                    + " líneas de venta): desactívala en lugar de eliminarla.");
        }
        rates.delete(id);
    }
}
