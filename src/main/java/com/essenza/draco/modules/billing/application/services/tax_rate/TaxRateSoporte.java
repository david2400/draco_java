package com.essenza.draco.modules.billing.application.services.tax_rate;

import java.util.Locale;

import org.springframework.stereotype.Component;

import com.essenza.draco.modules.billing.application.dto.tax_rate.SaveTaxRateDto;
import com.essenza.draco.modules.billing.application.dto.tax_rate.TaxRateDto;
import com.essenza.draco.modules.billing.application.output.repository.TaxRateRepository;
import com.essenza.draco.modules.billing.domain.model.TaxKind;
import com.essenza.draco.modules.billing.domain.model.TaxRate;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Component
class TaxRateSoporte {

    private final TaxRateRepository rates;

    TaxRateSoporte(TaxRateRepository rates) {
        this.rates = rates;
    }

    TaxRate get(Long id) {
        return rates.findById(id).orElseThrow(() -> new NotFoundException("No se encontró la tarifa de impuesto " + id + "."));
    }

    TaxRate toDomain(Long id, SaveTaxRateDto input, TaxRate current) {
        String code = input.getCode() == null ? null : input.getCode().trim().toUpperCase(Locale.ROOT);
        if (code != null && rates.existsCode(code, id)) {
            throw new ConflictException("Ya existe una tarifa con el código " + code + ".");
        }
        boolean isDefault = input.getIsDefault() != null ? input.getIsDefault() : current != null && current.isDefault();
        boolean active = input.getActive() != null ? input.getActive() : current == null || current.active();
        int position = input.getPosition() != null ? input.getPosition() : current == null ? 0 : current.position();
        return new TaxRate(id, code, input.getName(), blankToNull(input.getDescription()), TaxKind.parse(input.getKind()),
                input.getRate(), isDefault, active, position);
    }

    /** Si la tarifa queda por defecto, las demás dejan de serlo. */
    TaxRate saveKeepingSingleDefault(TaxRate rate) {
        TaxRate saved = rates.save(rate);
        if (saved.isDefault()) {
            rates.clearDefaultExcept(saved.id());
        }
        return saved;
    }

    TaxRateDto toDto(TaxRate rate) {
        return TaxRateDto.builder()
                .id(rate.id())
                .code(rate.code())
                .name(rate.name())
                .description(rate.description())
                .kind(rate.kind().name())
                .rate(rate.rate())
                .isDefault(rate.isDefault())
                .active(rate.active())
                .position(rate.position())
                .productsCount(rate.id() == null ? 0 : rates.productsUsing(rate.id()))
                .linesCount(rate.id() == null ? 0 : rates.linesUsing(rate.id()))
                .build();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
