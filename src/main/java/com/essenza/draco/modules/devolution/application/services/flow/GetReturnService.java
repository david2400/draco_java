package com.essenza.draco.modules.devolution.application.services.flow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.input.flow.GetReturnUseCase;
import com.essenza.draco.modules.devolution.application.output.repository.ReturnCaseRepository;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Devolución con sus líneas y las acciones del flujo disponibles. */
@Service
@Transactional(readOnly = true)
public class GetReturnService implements GetReturnUseCase {

    private final ReturnCaseRepository cases;
    private final ReturnSoporte soporte;

    public GetReturnService(ReturnCaseRepository cases, ReturnSoporte soporte) {
        this.cases = cases;
        this.soporte = soporte;
    }

    @Override
    public OrderDevolutionDto get(Long id) {
        return soporte.toDto(cases.find(id).orElseThrow(() -> new NotFoundException("No se encontró la devolución " + id + ".")));
    }
}
