package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.payment_type;
//import com.tuempresa.tuapp.api.mappers.sales.PaymentTypeMapper;
//import com.tuempresa.tuapp.domain.models.sales.PaymentType;
//import com.tuempresa.tuapp.domain.repositories.sales.PaymentTypeRepository;
//import com.tuempresa.tuapp.infrastructure.persistence.entities.sales.PaymentTypeEntity;

import com.essenza.draco.modules.sales.application.output.repository.PaymentTypeRepository;
import com.essenza.draco.modules.sales.domain.dto.payment_type.CreatePaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.PaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.UpdatePaymentTypeDto;
import com.essenza.draco.modules.sales.infrastructure.outbound.mappers.PaymentTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentTypeRepositoryAdapter implements PaymentTypeRepository {

    private final JpaPaymentTypeRepository jpa;
    private final PaymentTypeMapper mapper;

    public PaymentTypeRepositoryAdapter(JpaPaymentTypeRepository jpa, PaymentTypeMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }


    @Override
    public PaymentTypeDto create(CreatePaymentTypeDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public PaymentTypeDto update(Long id, UpdatePaymentTypeDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }

    @Override
    public Optional<PaymentTypeDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<PaymentTypeDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}
