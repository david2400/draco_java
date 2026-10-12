package com.essenza.draco.modules.sales.application.services.payment;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentFilter;
import com.essenza.draco.modules.sales.application.input.payment.SearchPaymentsUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentRepository;
import com.essenza.draco.shared.common.domain.dto.PageResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchPaymentsService implements SearchPaymentsUseCase {

    private final PaymentRepository payments;
    private final PaymentSoporte soporte;

    @Override
    public PageResponse<PaymentDto> search(PaymentFilter filter, Pageable pageable) {
        var page = payments.search(filter, pageable);
        var dtos = soporte.toDtos(page.getContent());
        return PageResponse.from(new org.springframework.data.domain.PageImpl<>(dtos, pageable, page.getTotalElements()));
    }
}
