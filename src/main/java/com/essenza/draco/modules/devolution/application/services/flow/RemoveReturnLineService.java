package com.essenza.draco.modules.devolution.application.services.flow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.devolution.application.input.flow.RemoveReturnLineUseCase;
import com.essenza.draco.modules.devolution.application.output.repository.OrderDevolutionDetailRepository;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Quita un producto de una devolución pendiente. */
@Service
@Transactional
public class RemoveReturnLineService implements RemoveReturnLineUseCase {

    private final ReturnSoporte soporte;
    private final OrderDevolutionDetailRepository details;

    public RemoveReturnLineService(ReturnSoporte soporte, OrderDevolutionDetailRepository details) {
        this.soporte = soporte;
        this.details = details;
    }

    @Override
    public boolean remove(Long detailId) {
        Long devolutionId = details.findById(detailId)
                .orElseThrow(() -> new NotFoundException("No se encontró la línea de devolución " + detailId + "."))
                .getOrderDevolutionId();
        ReturnCase returnCase = soporte.locked(devolutionId);
        returnCase.checkRemovable(detailId);
        return details.deleteById(detailId);
    }
}
