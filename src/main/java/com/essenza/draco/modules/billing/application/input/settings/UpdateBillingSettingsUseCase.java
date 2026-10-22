package com.essenza.draco.modules.billing.application.input.settings;

import com.essenza.draco.modules.billing.application.dto.settings.BillingSettingsDto;
import com.essenza.draco.modules.billing.application.dto.settings.SaveBillingSettingsDto;

public interface UpdateBillingSettingsUseCase {
    BillingSettingsDto update(SaveBillingSettingsDto input);
}
