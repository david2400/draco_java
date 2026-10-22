package com.essenza.draco.modules.sales.application.input.cash;

import com.essenza.draco.modules.sales.application.dto.cash.CashSessionDto;
import com.essenza.draco.modules.sales.application.dto.cash.OpenCashSessionDto;

public interface OpenCashSessionUseCase {
    CashSessionDto open(OpenCashSessionDto input);
}
