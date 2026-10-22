package com.essenza.draco.modules.devolution.application.services.flow;

import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.OrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.application.input.flow.ChangeReturnLineUseCase;
import com.essenza.draco.modules.devolution.application.output.repository.OrderDevolutionDetailRepository;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;
import com.essenza.draco.modules.devolution.domain.model.ReturnItem;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Cambia la cantidad u observación de un producto mientras la devolución está pendiente. */
@Service
@Transactional
public class ChangeReturnLineService implements ChangeReturnLineUseCase {

    private final ReturnSoporte soporte;
    private final OrderDevolutionDetailRepository details;

    public ChangeReturnLineService(ReturnSoporte soporte, OrderDevolutionDetailRepository details) {
        this.soporte = soporte;
        this.details = details;
    }

    @Override
    public OrderDevolutionDetailDto change(Long detailId, int quantity, String observation) {
        Long devolutionId = details.findById(detailId)
                .orElseThrow(() -> new NotFoundException("No se encontró la línea de devolución " + detailId + "."))
                .getOrderDevolutionId();
        ReturnCase returnCase = soporte.locked(devolutionId);
        ReturnItem current = returnCase.items().stream().filter(item -> Objects.equals(item.id(), detailId)).findFirst()
                .orElseThrow();
        var order = soporte.order(returnCase.orderId());
        ReturnSoporte.checkQuantity(soporte.line(order, current.productOrderId()), quantity,
                soporte.returnedUnits(order.id(), returnCase.id()));
        returnCase.changeItem(detailId, quantity, observation);
        ReturnCase saved = soporte.save(returnCase);
        return saved.items().stream().filter(item -> Objects.equals(item.id(), detailId)).findFirst()
                .map(item -> ReturnSoporte.detailDto(saved, item)).orElseThrow();
    }
}
