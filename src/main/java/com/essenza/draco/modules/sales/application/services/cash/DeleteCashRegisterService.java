package com.essenza.draco.modules.sales.application.services.cash;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.input.cash.DeleteCashRegisterUseCase;
import com.essenza.draco.modules.sales.application.output.repository.CashRegisterRepository;
import com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository;
import com.essenza.draco.modules.sales.domain.model.cash.CashRegister;
import com.essenza.draco.shared.exceptions.ConflictException;

@Service
@Transactional
public class DeleteCashRegisterService implements DeleteCashRegisterUseCase {

    private final CashRegisterRepository registers;
    private final CashSessionRepository sessions;
    private final CashSoporte soporte;

    public DeleteCashRegisterService(CashRegisterRepository registers, CashSessionRepository sessions, CashSoporte soporte) {
        this.registers = registers;
        this.sessions = sessions;
        this.soporte = soporte;
    }

    @Override
    public void delete(Long id) {
        CashRegister register = soporte.register(id);
        if (sessions.countByRegister(id) > 0) {
            throw new ConflictException("La caja " + register.name() + " tiene turnos registrados: desactívala en lugar de eliminarla.");
        }
        registers.delete(id);
    }
}
