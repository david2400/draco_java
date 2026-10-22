package com.essenza.draco.modules.devolution.application.services;

import com.essenza.draco.shared.exceptions.NotFoundException;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.modules.devolution.application.input.flow.EditReturnUseCase;
import com.essenza.draco.modules.devolution.application.input.flow.FindReturnableUseCase;
import com.essenza.draco.modules.devolution.application.input.flow.GetReturnUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution.*;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.CreateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.UpdateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.output.repository.MotiveDevolutionRepository;
import com.essenza.draco.modules.devolution.application.output.repository.OrderDevolutionRepository;
import com.essenza.draco.modules.devolution.domain.model.DevolutionState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * CRUD de la cabecera. Los cambios de estado van por las acciones del flujo
 * (aprobar, recibir, inspeccionar, reembolsar, rechazar); aquí solo datos generales.
 */
@Service
@Transactional
public class OrderDevolutionServiceImpl implements CreateOrderDevolutionUseCase,
        UpdateOrderDevolutionUseCase,
        DeleteOrderDevolutionUseCase,
        FindOrderDevolutionByIdUseCase,
        FindOrderDevolutionsUseCase {
    private final OrderDevolutionRepository orderDevolutionRepository;
    private final MotiveDevolutionRepository motives;
    private final FindReturnableUseCase returnable;
    private final EditReturnUseCase editReturn;
    private final GetReturnUseCase getReturn;

    public OrderDevolutionServiceImpl(OrderDevolutionRepository orderDevolutionRepository, MotiveDevolutionRepository motives,
                                      FindReturnableUseCase returnable, EditReturnUseCase editReturn, GetReturnUseCase getReturn) {
        this.orderDevolutionRepository = orderDevolutionRepository;
        this.motives = motives;
        this.returnable = returnable;
        this.editReturn = editReturn;
        this.getReturn = getReturn;
    }

    /** Cabecera sin líneas (pendiente); preferir POST /devolution/order_devolutions/request. */
    @Override
    public OrderDevolutionDto create(CreateOrderDevolutionDto input) {
        if (!returnable.forOrder(input.getOrderId(), null).returnable()) {
            throw new ConflictException("Solo se devuelven productos de órdenes pagadas, en proceso, enviadas o entregadas.");
        }
        if (motives.findById(input.getMotiveDevolutionId()).isEmpty()) {
            throw new NotFoundException("No se encontró el motivo de devolución " + input.getMotiveDevolutionId() + ".");
        }
        input.setState(DevolutionState.PENDING.code());
        input.setTotalRefundAmount(null);
        input.setApprovedBy(null);
        input.setApprovedAt(null);
        input.setReceivedBy(null);
        input.setReceivedAt(null);
        input.setInspectionNotes(null);
        OrderDevolutionDto created = orderDevolutionRepository.create(input);
        return getReturn.get(created.getId());
    }

    @Override
    public OrderDevolutionDto update(Long id, UpdateOrderDevolutionDto input) {
        OrderDevolutionDto current = orderDevolutionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No se encontró la devolución " + id + "."));
        if (input.getState() != null && !input.getState().isBlank()
                && !input.getState().trim().equalsIgnoreCase(current.getState())) {
            throw new ConflictException("El estado de la devolución cambia con sus acciones (aprobar, recibir, inspeccionar, "
                    + "reembolsar o rechazar), no editándola.");
        }
        if (input.getOrderId() != null && !Objects.equals(input.getOrderId(), current.getOrderId())) {
            throw new ConflictException("No se puede cambiar la orden de una devolución.");
        }
        return editReturn.edit(id, input.getMotiveDevolutionId(), input.getObservation(), input.getReturnMethodId(),
                input.getRefundMethodId(), input.getExternalReference());
    }

    @Override
    public boolean deleteById(Long id) {
        String state = orderDevolutionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No se encontró la devolución " + id + "."))
                .getState();
        if (state != null && !"P".equalsIgnoreCase(state) && !"X".equalsIgnoreCase(state)) {
            throw new ConflictException("La devolución " + id
                    + " ya está en proceso: solo se pueden eliminar devoluciones pendientes o rechazadas.");
        }
        return orderDevolutionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDevolutionDto> findById(Long id) {
        if (orderDevolutionRepository.findById(id).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(getReturn.get(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDevolutionDto> findAll() {
        return orderDevolutionRepository.findAll();
    }
}
