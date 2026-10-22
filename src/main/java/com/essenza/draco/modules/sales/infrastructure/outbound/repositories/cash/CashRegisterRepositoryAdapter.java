package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.cash;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.sales.application.output.repository.CashRegisterRepository;
import com.essenza.draco.modules.sales.domain.model.cash.CashRegister;
import com.essenza.draco.modules.sales.domain.model.cash.OpeningMode;
import com.essenza.draco.modules.sales.domain.model.cash.RegisterSettings;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.CashRegisterEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Repository
public class CashRegisterRepositoryAdapter implements CashRegisterRepository {

    private final JpaCashRegisterRepository jpa;

    public CashRegisterRepositoryAdapter(JpaCashRegisterRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<CashRegister> findAll() {
        return jpa.findAll().stream().map(CashRegisterRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<CashRegister> findById(Long id) {
        return jpa.findById(id).map(CashRegisterRepositoryAdapter::toDomain);
    }

    @Override
    public CashRegister save(CashRegister r) {
        CashRegisterEntity e = r.id() == null ? new CashRegisterEntity()
                : jpa.findById(r.id()).orElseThrow(() -> new NotFoundException("Caja no encontrada: " + r.id()));
        e.setCode(r.code());
        e.setName(r.name());
        e.setLocation(r.location());
        e.setWarehouseId(r.warehouseId());
        e.setActive(r.active());
        e.setStoreId(r.storeId());
        RegisterSettings c = r.settings();
        e.setOpeningMode(c.openingMode().name());
        e.setDefaultOpeningAmount(c.defaultOpeningAmount());
        e.setCashLimit(c.cashLimit());
        e.setBlindClose(c.blindClose());
        e.setDifferenceTolerance(c.differenceTolerance());
        e.setMaxDiscountPercent(c.maxDiscountPercent());
        e.setAutoInvoice(c.autoInvoice());
        e.setReceiptWidth(c.receiptWidth());
        e.setDeviceName(c.deviceName());
        e.getAllowedMethodIds().clear();
        e.getAllowedMethodIds().addAll(c.allowedMethodIds());
        return toDomain(jpa.saveAndFlush(e));
    }

    @Override
    public void delete(Long id) {
        jpa.findById(id).ifPresent(e -> {
            jpa.delete(e);
            jpa.flush();
        });
    }

    @Override
    public boolean existsCode(String code, Long excludeId) {
        return jpa.existsCode(code, excludeId);
    }

    static CashRegister toDomain(CashRegisterEntity e) {
        RegisterSettings settings = new RegisterSettings(new ArrayList<>(e.getAllowedMethodIds()),
                OpeningMode.parse(e.getOpeningMode()), e.getDefaultOpeningAmount(), e.getCashLimit(),
                Boolean.TRUE.equals(e.getBlindClose()), e.getDifferenceTolerance(), e.getMaxDiscountPercent(),
                Boolean.TRUE.equals(e.getAutoInvoice()), e.getReceiptWidth(), e.getDeviceName());
        return new CashRegister(e.getId(), e.getCode(), e.getName(), e.getLocation(), e.getWarehouseId(),
                !Boolean.FALSE.equals(e.getActive()), e.getStoreId(), settings);
    }
}
