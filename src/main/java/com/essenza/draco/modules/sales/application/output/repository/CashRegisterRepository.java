package com.essenza.draco.modules.sales.application.output.repository;

import java.util.List;
import java.util.Optional;

import com.essenza.draco.modules.sales.domain.model.cash.CashRegister;

public interface CashRegisterRepository {

    List<CashRegister> findAll();

    Optional<CashRegister> findById(Long id);

    CashRegister save(CashRegister register);

    void delete(Long id);

    boolean existsCode(String code, Long excludeId);
}
