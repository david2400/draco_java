package com.essenza.draco.modules.billing.application.output.repository;

import java.util.List;
import java.util.Optional;

import com.essenza.draco.modules.billing.domain.model.BillingEnvironment;
import com.essenza.draco.modules.billing.domain.model.DocumentKind;
import com.essenza.draco.modules.billing.domain.model.NumberingResolution;

public interface ResolutionRepository {

    List<NumberingResolution> findAll(DocumentKind kind);

    Optional<NumberingResolution> findById(Long id);

    /** Numeración activa del tipo y ambiente, bloqueada (consecutivo sin huecos ni duplicados). */
    Optional<NumberingResolution> lockActive(DocumentKind kind, BillingEnvironment environment);

    NumberingResolution save(NumberingResolution resolution);

    void delete(Long id);

    long documentsUsing(Long id);
}
