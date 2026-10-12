package com.essenza.draco.modules.sales.application.input.payment;

import org.springframework.data.domain.Pageable;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentFilter;
import com.essenza.draco.shared.common.domain.dto.PageResponse;

/** Listado paginado de pagos con filtros. */
public interface SearchPaymentsUseCase {

    PageResponse<PaymentDto> search(PaymentFilter filter, Pageable pageable);
}
