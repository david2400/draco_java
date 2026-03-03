package com.essenza.draco.modules.devolution.application.output.repository;

import com.essenza.draco.modules.devolution.domain.dto.refund_method.CreateRefundMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.RefundMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.UpdateRefundMethodDto;

import java.util.List;
import java.util.Optional;

public interface RefundMethodRepository {

    RefundMethodDto create(CreateRefundMethodDto input);

    RefundMethodDto update(Long id, UpdateRefundMethodDto input);

    boolean deleteById(Long id);

    Optional<RefundMethodDto> findById(Long id);

    List<RefundMethodDto> findAll();
}
