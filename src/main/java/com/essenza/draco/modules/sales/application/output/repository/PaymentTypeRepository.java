package com.essenza.draco.modules.sales.application.output.repository;

import com.essenza.draco.modules.sales.domain.dto.payment_type.CreatePaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.PaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.UpdatePaymentTypeDto;

import java.util.List;
import java.util.Optional;

public interface PaymentTypeRepository {
    PaymentTypeDto create(CreatePaymentTypeDto input);

    PaymentTypeDto update(Long id, UpdatePaymentTypeDto input);

    boolean deleteById(Long id);

    Optional<PaymentTypeDto> findById(Long id);

    List<PaymentTypeDto> findAll();
}
