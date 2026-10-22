package com.essenza.draco.modules.sales.application.services.cash;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.cash.CashRegisterDto;
import com.essenza.draco.modules.sales.application.dto.cash.SaveCashRegisterDto;
import com.essenza.draco.modules.sales.application.input.cash.UpdateCashRegisterUseCase;
import com.essenza.draco.modules.sales.application.output.repository.CashRegisterRepository;
import com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository;
import com.essenza.draco.modules.sales.domain.model.cash.CashRegister;
import com.essenza.draco.shared.exceptions.ConflictException;

/** Una caja con turno abierto no se desactiva (primero se cierra el turno). */
@Service
@Transactional
public class UpdateCashRegisterService implements UpdateCashRegisterUseCase {

    private final CashRegisterRepository registers;
    private final CashSessionRepository sessions;
    private final CashSoporte soporte;
    private final CashRegisterRules rules;

    public UpdateCashRegisterService(CashRegisterRepository registers, CashSessionRepository sessions, CashSoporte soporte,
                                     CashRegisterRules rules) {
        this.registers = registers;
        this.sessions = sessions;
        this.soporte = soporte;
        this.rules = rules;
    }

    @Override
    public CashRegisterDto update(Long id, SaveCashRegisterDto input) {
        CashRegister current = soporte.register(id);
        CashRegister next = rules.build(id, input, input.active() == null ? current.active() : input.active());
        if (sessions.findOpenByRegister(id).isPresent() && !java.util.Objects.equals(current.storeId(), next.storeId())) {
            throw new ConflictException("La caja " + current.name() + " tiene un turno abierto: ciérralo antes de cambiarla de punto de venta.");
        }
        if (registers.existsCode(next.code(), id)) {
            throw new ConflictException("Ya existe una caja con el código " + next.code() + ".");
        }
        if (!next.active() && sessions.findOpenByRegister(id).isPresent()) {
            throw new ConflictException("La caja " + current.name() + " tiene un turno abierto: ciérralo antes de desactivarla.");
        }
        return soporte.registerDto(registers.save(next));
    }
}
