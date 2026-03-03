package com.essenza.draco.modules.product_details.infrastructure.outbound.repositories.unit_measurement;

import com.essenza.draco.modules.product_details.application.output.repository.UnitMeasurementRepository;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.CreateUnitMeasurementDto;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UnitMeasurementDto;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UpdateUnitMeasurementDto;
import com.essenza.draco.modules.product_details.infrastructure.outbound.mappers.UnitMeasurementMapper;
import com.essenza.draco.modules.product_details.infrastructure.outbound.persistence.mysql.shop.UnitMeasurementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UnitMeasurementRepositoryAdapter implements UnitMeasurementRepository {

    private final JpaUnitMeasurementRepository jpa;
    private final UnitMeasurementMapper mapper;

    public UnitMeasurementRepositoryAdapter(JpaUnitMeasurementRepository jpa, UnitMeasurementMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }


    @Override
    public UnitMeasurementDto create(CreateUnitMeasurementDto input) {
        UnitMeasurementEntity saved = jpa.save(mapper.toEntity(input));
        return mapper.toDto(saved);
    }

    @Override
    public UnitMeasurementDto update(Long id, UpdateUnitMeasurementDto input) {
        UnitMeasurementEntity saved = jpa.save(mapper.toEntity(input));
        return mapper.toDto(saved);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }


    @Override
    public Optional<UnitMeasurementDto> findById(Long id) { return jpa.findById(id).map(mapper::toDto); }

    @Override
    public List<UnitMeasurementDto> findAll() { return jpa.findAll().stream().map(mapper::toDto).toList(); }
}
