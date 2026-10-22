package com.essenza.draco.modules.devolution.application.services.flow;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.devolution.application.dto.flow.ReceiveReturnDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.input.flow.ReceiveReturnUseCase;
import com.essenza.draco.modules.devolution.domain.model.DevolutionRuleViolation;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;

@Service
@Transactional
public class ReceiveReturnService implements ReceiveReturnUseCase {

    private final ReturnSoporte soporte;

    public ReceiveReturnService(ReturnSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    public OrderDevolutionDto receive(Long id, ReceiveReturnDto input) {
        ReturnCase returnCase = soporte.locked(id);
        Map<Long, Integer> received = new HashMap<>();
        if (input != null && input.lines() != null) {
            for (ReceiveReturnDto.Line line : input.lines()) {
                if (received.put(line.detailId(), line.receivedQuantity()) != null) {
                    throw new DevolutionRuleViolation("La línea " + line.detailId() + " está repetida.");
                }
            }
        }
        returnCase.receive(input == null ? null : input.by(), received, input == null ? null : input.notes(),
                ReturnSoporte.now());
        return soporte.toDto(soporte.save(returnCase));
    }
}
