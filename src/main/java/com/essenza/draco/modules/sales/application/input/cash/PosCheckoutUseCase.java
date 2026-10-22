package com.essenza.draco.modules.sales.application.input.cash;

import com.essenza.draco.modules.sales.application.dto.cash.PosCheckoutDto;
import com.essenza.draco.modules.sales.application.dto.cash.PosCheckoutResultDto;

public interface PosCheckoutUseCase {
    PosCheckoutResultDto checkout(PosCheckoutDto input);
}
