package com.essenza.draco.modules.sales.application.services.payment;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentFilter;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentsSummaryDto;
import com.essenza.draco.modules.sales.application.input.payment.SummarizePaymentsUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;
import com.essenza.draco.modules.sales.application.output.repository.PaymentRepository;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SummarizePaymentsService implements SummarizePaymentsUseCase {

    private final PaymentRepository payments;
    private final PaymentMethodRepository methods;

    @Override
    public PaymentsSummaryDto summarize(PaymentFilter filter) {
        PaymentRepository.Totals totals = payments.totals(filter);
        Map<Long, PaymentMethod> byId = methods.findAll().stream()
                .collect(Collectors.toMap(PaymentMethod::id, Function.identity(), (a, b) -> a));
        return new PaymentsSummaryDto(totals.count(), totals.approved(), totals.pending(), totals.refunded(), totals.fees(),
                totals.approved().subtract(totals.refunded()).subtract(totals.fees()),
                totals.byMethod().stream().map(row -> {
                    PaymentMethod method = byId.get(row.methodId());
                    return new PaymentsSummaryDto.ByMethod(row.methodId(), method == null ? "#" + row.methodId() : method.name(),
                            method == null ? null : method.channel().name(), row.count(), row.approved(), row.refunded(),
                            row.fees());
                }).toList(),
                totals.byStatus().stream()
                        .map(row -> new PaymentsSummaryDto.ByStatus(row.status(), row.count(), row.amount()))
                        .toList());
    }
}
