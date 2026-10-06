package com.essenza.draco.modules.devolution.application.services;

import com.essenza.draco.shared.exceptions.NotFoundException;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.modules.devolution.application.input.order_devolution.*;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.CreateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.UpdateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.output.repository.OrderDevolutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderDevolutionServiceImpl implements CreateOrderDevolutionUseCase,
        UpdateOrderDevolutionUseCase,
        DeleteOrderDevolutionUseCase,
        FindOrderDevolutionByIdUseCase,
        FindOrderDevolutionsUseCase {
    private final OrderDevolutionRepository orderDevolutionRepository;

    public OrderDevolutionServiceImpl(OrderDevolutionRepository orderDevolutionRepository) {
        this.orderDevolutionRepository = orderDevolutionRepository;
    }


    @Override
    public OrderDevolutionDto create(CreateOrderDevolutionDto input) {
        return orderDevolutionRepository.create(input);
    }

    @Override
    public OrderDevolutionDto update(Long id, UpdateOrderDevolutionDto input) {
        return orderDevolutionRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        String state = orderDevolutionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No se encontró la devolución " + id + "."))
                .getState();
        if (state != null && !"P".equals(state) && !"X".equals(state)) {
            throw new ConflictException("La devolución " + id
                    + " ya está en proceso: solo se pueden eliminar devoluciones pendientes o rechazadas.");
        }
        return orderDevolutionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDevolutionDto> findById(Long id) {
        return orderDevolutionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDevolutionDto> findAll() {
        return orderDevolutionRepository.findAll();
    }
}

