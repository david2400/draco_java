package com.essenza.draco.modules.sales.application.input.cash;

import com.essenza.draco.modules.sales.application.dto.cash.CashRegisterDto;
import com.essenza.draco.modules.sales.application.dto.cash.SaveCashRegisterDto;

public interface CreateCashRegisterUseCase {
    CashRegisterDto create(SaveCashRegisterDto input);
}
