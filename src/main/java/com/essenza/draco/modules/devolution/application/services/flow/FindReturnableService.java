package com.essenza.draco.modules.devolution.application.services.flow;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.devolution.application.dto.flow.ReturnableOrderDto;
import com.essenza.draco.modules.devolution.application.input.flow.FindReturnableUseCase;
import com.essenza.draco.shared.common.sales.OrderReturns;
import com.essenza.draco.shared.common.sales.OrderReturns.ReturnableOrder;

/** Qué se puede devolver de una orden (para armar la solicitud). */
@Service
@Transactional(readOnly = true)
public class FindReturnableService implements FindReturnableUseCase {

    private final ReturnSoporte soporte;
    private final OrderReturns orders;

    public FindReturnableService(ReturnSoporte soporte, OrderReturns orders) {
        this.soporte = soporte;
        this.orders = orders;
    }

    @Override
    public ReturnableOrderDto forOrder(Long orderId, Long excludeDevolutionId) {
        ReturnableOrder order = soporte.order(orderId);
        Map<Long, Integer> returned = soporte.returnedUnits(orderId, excludeDevolutionId);
        return new ReturnableOrderDto(order.id(), order.state(), ReturnSoporte.RETURNABLE_STATES.contains(order.state()),
                order.total(), order.returnedAmount(), orders.refundable(orderId),
                order.lines().stream().map(line -> {
                    int already = returned.getOrDefault(line.lineId(), 0);
                    return new ReturnableOrderDto.Line(line.lineId(), line.productId(), line.skuId(), line.skuCode(),
                            line.productName(), line.quantity(), already, Math.max(0, line.quantity() - already),
                            line.unitPrice(), line.total());
                }).toList());
    }
}
