package com.essenza.draco.modules.sales.application.services.cash;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.cash.CashSessionDto;
import com.essenza.draco.modules.sales.application.dto.cash.CashSessionFilter;
import com.essenza.draco.modules.sales.application.input.cash.SearchCashSessionsUseCase;
import com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository;

@Service
@Transactional(readOnly = true)
public class SearchCashSessionsService implements SearchCashSessionsUseCase {

    private final CashSessionRepository sessions;
    private final CashSoporte soporte;

    public SearchCashSessionsService(CashSessionRepository sessions, CashSoporte soporte) {
        this.sessions = sessions;
        this.soporte = soporte;
    }

    @Override
    public Page<CashSessionDto> search(CashSessionFilter filter, Pageable pageable) {
        return sessions.search(filter, pageable).map(session -> soporte.toDto(session, false));
    }
}
