package com.essenza.draco.modules.billing.application.services.tax_rate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.tax_rate.SaveTaxRateDto;
import com.essenza.draco.modules.billing.application.dto.tax_rate.TaxRateDto;
import com.essenza.draco.modules.billing.application.input.tax_rate.UpdateTaxRateUseCase;
import com.essenza.draco.modules.billing.domain.model.TaxRate;
import com.essenza.draco.shared.exceptions.ConflictException;

/**
 * Cambiar el porcentaje solo afecta las ventas nuevas: las líneas existentes conservan
 * el impuesto congelado. La tarifa por defecto deja de serlo solo marcando otra.
 */
@Service
@Transactional
public class UpdateTaxRateService implements UpdateTaxRateUseCase {

    private final TaxRateSoporte soporte;

    public UpdateTaxRateService(TaxRateSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    public TaxRateDto update(Long id, SaveTaxRateDto input) {
        TaxRate current = soporte.get(id);
        TaxRate next = soporte.toDomain(id, input, current);
        if (current.isDefault() && !next.isDefault()) {
            throw new ConflictException("\"" + current.name() + "\" es la tarifa por defecto: marca otra como por defecto en su lugar.");
        }
        return soporte.toDto(soporte.saveKeepingSingleDefault(next));
    }
}
