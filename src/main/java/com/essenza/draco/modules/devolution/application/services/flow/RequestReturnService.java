package com.essenza.draco.modules.devolution.application.services.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.devolution.application.dto.flow.ReturnRequestDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.input.flow.RequestReturnUseCase;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;
import com.essenza.draco.modules.devolution.domain.model.ReturnItem;
import com.essenza.draco.shared.common.sales.OrderReturns.ReturnableOrder;

/** Solicitud de devolución con sus líneas: valida la orden y lo que se puede devolver. */
@Service
@Transactional
public class RequestReturnService implements RequestReturnUseCase {

    private final ReturnSoporte soporte;

    public RequestReturnService(ReturnSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    public OrderDevolutionDto request(ReturnRequestDto input) {
        ReturnableOrder order = soporte.returnableOrder(input.orderId());
        soporte.checkCatalogs(input.motiveDevolutionId(), input.returnMethodId(), input.refundMethodId());
        Map<Long, Integer> returned = soporte.returnedUnits(order.id(), null);
        List<ReturnItem> items = new ArrayList<>();
        for (ReturnRequestDto.Line line : input.lines()) {
            items.add(soporte.item(order, line.productOrderId(), line.quantity(), line.observation(), returned));
        }
        ReturnCase created = soporte.save(ReturnCase.request(order.id(), input.motiveDevolutionId(), input.observation(),
                input.returnMethodId(), input.refundMethodId(), input.externalReference(), items));
        return soporte.toDto(created);
    }
}
