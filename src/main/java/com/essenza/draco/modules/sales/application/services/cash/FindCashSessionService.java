package com.essenza.draco.modules.sales.application.services.cash;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.cash.CashSessionDto;
import com.essenza.draco.modules.sales.application.input.cash.FindCashSessionUseCase;

@Service
@Transactional(readOnly = true)
public class FindCashSessionService implements FindCashSessionUseCase {

    private final CashSoporte soporte;

    public FindCashSessionService(CashSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    public CashSessionDto findById(Long sessionId) {
        return soporte.toDto(soporte.session(sessionId), true);
    }
}
