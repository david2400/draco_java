package com.essenza.draco.modules.sales.application.input.cash;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.essenza.draco.modules.sales.application.dto.cash.CashSessionDto;
import com.essenza.draco.modules.sales.application.dto.cash.CashSessionFilter;

public interface SearchCashSessionsUseCase {
    Page<CashSessionDto> search(CashSessionFilter filter, Pageable pageable);
}
