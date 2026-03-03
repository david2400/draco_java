package com.essenza.draco.modules.sales.application.services;

import com.essenza.draco.modules.sales.application.input.payment_type.*;
import com.essenza.draco.modules.sales.domain.dto.payment_type.CreatePaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.PaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.UpdatePaymentTypeDto;
import com.essenza.draco.modules.sales.infrastructure.outbound.repositories.payment_type.PaymentTypeRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentTypeServiceImpl implements 
        CreatePaymentTypeUseCase,
        UpdatePaymentTypeUseCase,
        DeletePaymentTypeUseCase,
        FindPaymentTypeByIdUseCase,
        FindPaymentTypesUseCase {

    private final PaymentTypeRepositoryAdapter paymentTypeRepository;

    public PaymentTypeServiceImpl(PaymentTypeRepositoryAdapter paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Override
    public PaymentTypeDto create(CreatePaymentTypeDto input) {
        return paymentTypeRepository.create(input);
    }


    @Override
    public PaymentTypeDto update(Long id, UpdatePaymentTypeDto input) {
        return paymentTypeRepository.update(id,input);
    }

    @Override
    public boolean deleteById(Long id) {
//        if (!paymentTypeRepository.existsById(id)) return false;
//        paymentTypeRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentTypeDto> findById(Long id) {
        return paymentTypeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTypeDto> findAll() {
        return paymentTypeRepository.findAll();
    }
}
