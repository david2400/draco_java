package com.essenza.draco.modules.sales.application.input.cash;

import com.essenza.draco.modules.sales.application.dto.cash.CashSessionDto;

public interface FindCashSessionUseCase {
    CashSessionDto findById(Long sessionId);
}
