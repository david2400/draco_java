package com.essenza.draco.modules.sales.application.services.cash;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.cash.CashRegisterDto;
import com.essenza.draco.modules.sales.application.input.cash.FindCashRegistersUseCase;
import com.essenza.draco.modules.sales.application.output.repository.CashRegisterRepository;
import com.essenza.draco.modules.sales.domain.model.cash.CashRegister;

@Service
@Transactional(readOnly = true)
public class FindCashRegistersService implements FindCashRegistersUseCase {

    private final CashRegisterRepository registers;
    private final CashSoporte soporte;

    public FindCashRegistersService(CashRegisterRepository registers, CashSoporte soporte) {
        this.registers = registers;
        this.soporte = soporte;
    }

    @Override
    public List<CashRegisterDto> findAll(boolean includeInactive) {
        return registers.findAll().stream()
                .filter(r -> includeInactive || r.active())
                .sorted(Comparator.comparing(CashRegister::code))
                .map(soporte::registerDto)
                .toList();
    }
}
