package com.essenza.draco.modules.sales.application.services.cash;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.cash.CashRegisterDto;
import com.essenza.draco.modules.sales.application.dto.cash.SaveCashRegisterDto;
import com.essenza.draco.modules.sales.application.input.cash.CreateCashRegisterUseCase;
import com.essenza.draco.modules.sales.application.output.repository.CashRegisterRepository;
import com.essenza.draco.modules.sales.domain.model.cash.CashRegister;
import com.essenza.draco.shared.exceptions.ConflictException;

@Service
@Transactional
public class CreateCashRegisterService implements CreateCashRegisterUseCase {

    private final CashRegisterRepository registers;
    private final CashSoporte soporte;
    private final CashRegisterRules rules;

    public CreateCashRegisterService(CashRegisterRepository registers, CashSoporte soporte, CashRegisterRules rules) {
        this.registers = registers;
        this.soporte = soporte;
        this.rules = rules;
    }

    @Override
    public CashRegisterDto create(SaveCashRegisterDto input) {
        CashRegister register = rules.build(null, input, input.active() == null || input.active());
        if (registers.existsCode(register.code(), null)) {
            throw new ConflictException("Ya existe una caja con el código " + register.code() + ".");
        }
        return soporte.registerDto(registers.save(register));
    }
}
