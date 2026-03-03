package com.essenza.draco.modules.product_details.application.services;

import com.essenza.draco.modules.product_details.application.input.unit_measurement.*;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.CreateUnitMeasurementDto;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UnitMeasurementDto;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UpdateUnitMeasurementDto;
import com.essenza.draco.modules.product_details.infrastructure.outbound.repositories.unit_measurement.UnitMeasurementRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class UnitMeasurementServiceImpl implements CreateUnitMeasurementUseCase, UpdateUnitMeasurementUseCase, DeleteUnitMeasurementUseCase, FindUnitMeasurementsUseCase, FindUnitMeasurementByIdUseCase {

    private final UnitMeasurementRepositoryAdapter unitMeasurementRepository;

//    public UnitMeasurementServiceImpl(UnitMeasurementRepositoryAdapter unitMeasurementRepository) {
//        this.unitMeasurementRepository = unitMeasurementRepository;
//    }


    @Override
    public UnitMeasurementDto create(CreateUnitMeasurementDto input) {
        // UnitEntity por ahora solo tiene id; creamos un registro vacío

        return unitMeasurementRepository.create(input);
    }

    @Override
    public UnitMeasurementDto update(Long id, UpdateUnitMeasurementDto input) {
        // Sin campos por actualizar actualmente
        return unitMeasurementRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
//        if (!unitRepository.existsById(id)) return false;
//        unitRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnitMeasurementDto> findById(Long id) {
        return unitMeasurementRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnitMeasurementDto> findAll() {
        return unitMeasurementRepository.findAll();
    }
}
