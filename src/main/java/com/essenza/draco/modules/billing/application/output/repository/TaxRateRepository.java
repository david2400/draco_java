package com.essenza.draco.modules.billing.application.output.repository;

import java.util.List;
import java.util.Optional;

import com.essenza.draco.modules.billing.domain.model.TaxRate;

public interface TaxRateRepository {

    List<TaxRate> findAll();

    Optional<TaxRate> findById(Long id);

    Optional<TaxRate> findDefault();

    TaxRate save(TaxRate rate);

    void delete(Long id);

    boolean existsCode(String code, Long excludeId);

    /** Quita la marca de "por defecto" a las demás tarifas. */
    void clearDefaultExcept(Long id);

    long productsUsing(Long id);

    long linesUsing(Long id);
}
