package com.essenza.draco.modules.devolution.application.services.flow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.devolution.application.dto.flow.RejectReturnDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.input.flow.RejectReturnUseCase;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;

/** Rechazo (antes de inspeccionar): no mueve stock ni dinero; las unidades vuelven a ser devolvibles. */
@Service
@Transactional
public class RejectReturnService implements RejectReturnUseCase {

    private final ReturnSoporte soporte;

    public RejectReturnService(ReturnSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    public OrderDevolutionDto reject(Long id, RejectReturnDto input) {
        ReturnCase returnCase = soporte.locked(id);
        returnCase.reject(input.by(), input.reason(), ReturnSoporte.now());
        return soporte.toDto(soporte.save(returnCase));
    }
}
