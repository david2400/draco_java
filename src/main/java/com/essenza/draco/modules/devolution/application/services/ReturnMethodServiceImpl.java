package com.essenza.draco.modules.devolution.application.services;

import com.essenza.draco.modules.devolution.application.input.return_method.*;
import com.essenza.draco.modules.devolution.domain.dto.return_method.CreateReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.ReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.UpdateReturnMethodDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.return_method.ReturnMethodRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReturnMethodServiceImpl implements CreateReturnMethodUseCase,
        UpdateReturnMethodUseCase,
        DeleteReturnMethodUseCase,
        FindReturnMethodByIdUseCase,
        FindReturnMethodsUseCase {

    private final ReturnMethodRepositoryAdapter returnMethodRepository;

    public ReturnMethodServiceImpl(ReturnMethodRepositoryAdapter returnMethodRepository) {
        this.returnMethodRepository = returnMethodRepository;
    }

    @Override
    public ReturnMethodDto create(CreateReturnMethodDto input) {
        return returnMethodRepository.create(input);
    }

    @Override
    public ReturnMethodDto update(Long id, UpdateReturnMethodDto input) {
        return returnMethodRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return returnMethodRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReturnMethodDto> findById(Long id) {
        return returnMethodRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnMethodDto> findAll() {
        return returnMethodRepository.findAll();
    }
}
