package com.essenza.draco.modules.devolution.application.services;

import com.essenza.draco.modules.devolution.application.input.refund_method.*;
import com.essenza.draco.modules.devolution.application.dto.refund_method.CreateRefundMethodDto;
import com.essenza.draco.modules.devolution.application.dto.refund_method.RefundMethodDto;
import com.essenza.draco.modules.devolution.application.dto.refund_method.UpdateRefundMethodDto;
import com.essenza.draco.modules.devolution.application.output.repository.RefundMethodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RefundMethodServiceImpl implements CreateRefundMethodUseCase,
        UpdateRefundMethodUseCase,
        DeleteRefundMethodUseCase,
        FindRefundMethodByIdUseCase,
        FindRefundMethodsUseCase {

    private final RefundMethodRepository refundMethodRepository;

    public RefundMethodServiceImpl(RefundMethodRepository refundMethodRepository) {
        this.refundMethodRepository = refundMethodRepository;
    }

    @Override
    public RefundMethodDto create(CreateRefundMethodDto input) {
        return refundMethodRepository.create(input);
    }

    @Override
    public RefundMethodDto update(Long id, UpdateRefundMethodDto input) {
        return refundMethodRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return refundMethodRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefundMethodDto> findById(Long id) {
        return refundMethodRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundMethodDto> findAll() {
        return refundMethodRepository.findAll();
    }
}
