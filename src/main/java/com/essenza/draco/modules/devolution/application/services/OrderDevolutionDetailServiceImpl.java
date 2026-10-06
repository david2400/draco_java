package com.essenza.draco.modules.devolution.application.services;

import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.*;
import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.CreateOrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.OrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.UpdateOrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.application.output.repository.OrderDevolutionDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderDevolutionDetailServiceImpl implements CreateOrderDevolutionDetailUseCase,
        UpdateOrderDevolutionDetailUseCase,
        DeleteOrderDevolutionDetailUseCase,
        FindOrderDevolutionDetailByIdUseCase,
        FindOrderDevolutionDetailsUseCase {
    private final OrderDevolutionDetailRepository orderDevolutionDetailRepository;

    public OrderDevolutionDetailServiceImpl(OrderDevolutionDetailRepository orderDevolutionDetailRepository) {
        this.orderDevolutionDetailRepository = orderDevolutionDetailRepository;
    }


    @Override
    public OrderDevolutionDetailDto create(CreateOrderDevolutionDetailDto input) {
        return orderDevolutionDetailRepository.create(input);
    }

    @Override
    public OrderDevolutionDetailDto update(Long id, UpdateOrderDevolutionDetailDto input) {
        return orderDevolutionDetailRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return orderDevolutionDetailRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDevolutionDetailDto> findById(Long id) {
        return orderDevolutionDetailRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDevolutionDetailDto> findAll() {
        return orderDevolutionDetailRepository.findAll();
    }
}

