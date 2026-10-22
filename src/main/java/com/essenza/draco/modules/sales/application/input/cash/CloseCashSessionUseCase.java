package com.essenza.draco.modules.sales.application.input.cash;

import com.essenza.draco.modules.sales.application.dto.cash.CashSessionDto;
import com.essenza.draco.modules.sales.application.dto.cash.CloseCashSessionDto;

public interface CloseCashSessionUseCase {
    CashSessionDto close(Long sessionId, CloseCashSessionDto input);
}
