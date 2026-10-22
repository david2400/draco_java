package com.essenza.draco.modules.devolution.application.services.flow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.devolution.application.dto.flow.ApproveReturnDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.input.flow.ApproveReturnUseCase;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;

@Service
@Transactional
public class ApproveReturnService implements ApproveReturnUseCase {

    private final ReturnSoporte soporte;

    public ApproveReturnService(ReturnSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    public OrderDevolutionDto approve(Long id, ApproveReturnDto input) {
        ReturnCase returnCase = soporte.locked(id);
        Long method = input == null ? null : input.returnMethodId();
        soporte.checkCatalogs(null, method, null);
        returnCase.approve(input == null ? null : input.by(), method, ReturnSoporte.now());
        return soporte.toDto(soporte.save(returnCase));
    }
}
