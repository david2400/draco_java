package com.essenza.draco.modules.billing.application.output.repository;

import com.essenza.draco.modules.billing.domain.model.BillingSettings;

public interface BillingSettingsRepository {

    BillingSettings get();

    BillingSettings save(BillingSettings settings);
}
