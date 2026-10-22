package com.essenza.draco.modules.devolution.application.services.flow;

import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.OrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.application.input.flow.AddReturnLineUseCase;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;

/** Agrega un producto a una devolución pendiente (precio y nombre congelados de la orden). */
@Service
@Transactional
public class AddReturnLineService implements AddReturnLineUseCase {

    private final ReturnSoporte soporte;

    public AddReturnLineService(ReturnSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    public OrderDevolutionDetailDto add(Long devolutionId, Long productOrderId, int quantity, String observation) {
        ReturnCase returnCase = soporte.locked(devolutionId);
        var order = soporte.order(returnCase.orderId());
        returnCase.addItem(soporte.item(order, productOrderId, quantity, observation,
                soporte.returnedUnits(order.id(), returnCase.id())));
        ReturnCase saved = soporte.save(returnCase);
        return saved.items().stream().filter(item -> Objects.equals(item.productOrderId(), productOrderId)).findFirst()
                .map(item -> ReturnSoporte.detailDto(saved, item)).orElseThrow();
    }
}
