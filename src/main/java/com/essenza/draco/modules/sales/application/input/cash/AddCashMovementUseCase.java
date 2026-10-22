package com.essenza.draco.modules.sales.application.input.cash;

import com.essenza.draco.modules.sales.application.dto.cash.CashMovementDto;
import com.essenza.draco.modules.sales.application.dto.cash.CreateCashMovementDto;

public interface AddCashMovementUseCase {
    CashMovementDto add(Long sessionId, CreateCashMovementDto input);
}
