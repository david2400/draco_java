package com.essenza.draco.modules.devolution.application.services.flow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.input.flow.EditReturnUseCase;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;

/** Datos generales (motivo, observación, formas de devolución/reembolso) mientras está pendiente o aprobada. */
@Service
@Transactional
public class EditReturnService implements EditReturnUseCase {

    private final ReturnSoporte soporte;

    public EditReturnService(ReturnSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    public OrderDevolutionDto edit(Long id, Long motiveId, String observation, Long returnMethodId, Long refundMethodId,
                                   String externalReference) {
        ReturnCase returnCase = soporte.locked(id);
        soporte.checkCatalogs(motiveId, returnMethodId, refundMethodId);
        returnCase.edit(motiveId, observation, returnMethodId, refundMethodId, externalReference);
        return soporte.toDto(soporte.save(returnCase));
    }
}
