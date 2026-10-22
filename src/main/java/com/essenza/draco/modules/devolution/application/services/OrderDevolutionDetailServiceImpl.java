package com.essenza.draco.modules.devolution.application.services;

import com.essenza.draco.modules.devolution.application.input.flow.AddReturnLineUseCase;
import com.essenza.draco.modules.devolution.application.input.flow.ChangeReturnLineUseCase;
import com.essenza.draco.modules.devolution.application.input.flow.RemoveReturnLineUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.*;
import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.CreateOrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.OrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.UpdateOrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.application.output.repository.OrderDevolutionDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Líneas de la devolución. Solo se agregan, cambian o quitan mientras está pendiente; el
 * precio, SKU y nombre se toman de la línea de la orden. Recepción e inspección van por el flujo.
 */
@Service
@Transactional
public class OrderDevolutionDetailServiceImpl implements CreateOrderDevolutionDetailUseCase,
        UpdateOrderDevolutionDetailUseCase,
        DeleteOrderDevolutionDetailUseCase,
        FindOrderDevolutionDetailByIdUseCase,
        FindOrderDevolutionDetailsUseCase {
    private final OrderDevolutionDetailRepository orderDevolutionDetailRepository;
    private final AddReturnLineUseCase addLine;
    private final ChangeReturnLineUseCase changeLine;
    private final RemoveReturnLineUseCase removeLine;

    public OrderDevolutionDetailServiceImpl(OrderDevolutionDetailRepository orderDevolutionDetailRepository,
                                            AddReturnLineUseCase addLine, ChangeReturnLineUseCase changeLine,
                                            RemoveReturnLineUseCase removeLine) {
        this.orderDevolutionDetailRepository = orderDevolutionDetailRepository;
        this.addLine = addLine;
        this.changeLine = changeLine;
        this.removeLine = removeLine;
    }

    @Override
    public OrderDevolutionDetailDto create(CreateOrderDevolutionDetailDto input) {
        return addLine.add(input.getOrderDevolutionId(), input.getProductOrderId(), input.getQuantity(), input.getObservation());
    }

    @Override
    public OrderDevolutionDetailDto update(Long id, UpdateOrderDevolutionDetailDto input) {
        return changeLine.change(id, input.getQuantity(), input.getObservation());
    }

    @Override
    public boolean deleteById(Long id) {
        return removeLine.remove(id);
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
