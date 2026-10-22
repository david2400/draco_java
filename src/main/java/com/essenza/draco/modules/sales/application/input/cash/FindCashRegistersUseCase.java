package com.essenza.draco.modules.sales.application.input.cash;

import java.util.List;

import com.essenza.draco.modules.sales.application.dto.cash.CashRegisterDto;

public interface FindCashRegistersUseCase {
    List<CashRegisterDto> findAll(boolean includeInactive);
}
