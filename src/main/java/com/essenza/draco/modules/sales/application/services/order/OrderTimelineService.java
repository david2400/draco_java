package com.essenza.draco.modules.sales.application.services.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.order.OrderActionDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderEventDto;
import com.essenza.draco.modules.sales.application.input.order.OrderTimelineUseCase;
import com.essenza.draco.modules.sales.domain.model.order.OrderEventType;
import com.essenza.draco.modules.sales.domain.model.order.OrderRuleViolation;

/** Línea de tiempo y notas internas de la orden. */
@Service
@Transactional
public class OrderTimelineService implements OrderTimelineUseCase {

    private final OrderSoporte soporte;

    public OrderTimelineService(OrderSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEventDto> timeline(Long orderId) {
        soporte.details(orderId);
        return soporte.timeline(orderId);
    }

    @Override
    public OrderEventDto addNote(Long orderId, OrderActionDto input) {
        soporte.details(orderId);
        String note = input == null ? null : OrderSoporte.blankToNull(input.note());
        if (note == null) {
            throw new OrderRuleViolation("Escribe la nota.");
        }
        soporte.log(orderId, OrderEventType.NOTE, null, null, note, input.actor());
        List<OrderEventDto> events = soporte.timeline(orderId);
        return events.get(events.size() - 1);
    }
}
