package com.essenza.draco.modules.sales.application.services.cash;

import java.util.List;

import org.springframework.stereotype.Component;

import com.essenza.draco.modules.sales.application.dto.cash.SaveCashRegisterDto;
import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;
import com.essenza.draco.modules.sales.application.output.repository.StoreRepository;
import com.essenza.draco.modules.sales.domain.model.cash.CashRegister;
import com.essenza.draco.modules.sales.domain.model.cash.CashRuleViolation;
import com.essenza.draco.modules.sales.domain.model.cash.OpeningMode;
import com.essenza.draco.modules.sales.domain.model.cash.RegisterSettings;
import com.essenza.draco.modules.sales.domain.model.cash.Store;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Arma y valida una caja desde el formulario: punto de venta, bodega, medios de pago y reglas. */
@Component
class CashRegisterRules {

    private final StoreRepository stores;
    private final PaymentMethodRepository methods;

    CashRegisterRules(StoreRepository stores, PaymentMethodRepository methods) {
        this.stores = stores;
        this.methods = methods;
    }

    CashRegister build(Long id, SaveCashRegisterDto input, boolean active) {
        if (input.storeId() == null) {
            throw new CashRuleViolation("Elige el punto de venta de la caja.");
        }
        Store store = stores.findById(input.storeId())
                .orElseThrow(() -> new NotFoundException("No se encontró el punto de venta " + input.storeId() + "."));
        if (!store.active() && active) {
            throw new CashRuleViolation("El punto de venta \"" + store.name() + "\" está inactivo: actívalo o elige otro.");
        }
        if (input.warehouseId() != null && !stores.warehouseExists(input.warehouseId())) {
            throw new NotFoundException("No se encontró la bodega " + input.warehouseId() + ".");
        }
        List<Long> allowed = input.allowedMethodIds() == null ? List.of() : input.allowedMethodIds();
        for (Long methodId : allowed) {
            PaymentMethod method = methods.findById(methodId)
                    .orElseThrow(() -> new NotFoundException("No se encontró el medio de pago " + methodId + "."));
            if (!method.active()) {
                throw new CashRuleViolation("El medio de pago \"" + method.name() + "\" está inactivo.");
            }
            if (method.isGateway()) {
                throw new CashRuleViolation("\"" + method.name() + "\" es una pasarela en línea: no se cobra en caja.");
            }
        }
        RegisterSettings settings = new RegisterSettings(allowed, OpeningMode.parse(input.openingMode()),
                input.defaultOpeningAmount(), input.cashLimit(), Boolean.TRUE.equals(input.blindClose()),
                input.differenceTolerance(), input.maxDiscountPercent(), Boolean.TRUE.equals(input.autoInvoice()),
                input.receiptWidth(), input.deviceName());
        return new CashRegister(id, input.code(), input.name(), input.location(), input.warehouseId(), active, store.id(), settings);
    }
}
