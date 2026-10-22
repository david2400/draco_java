package com.essenza.draco.modules.sales.application.services.cash;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.essenza.draco.modules.sales.application.dto.cash.CashMovementDto;
import com.essenza.draco.modules.sales.application.dto.cash.CashRegisterDto;
import com.essenza.draco.modules.sales.application.dto.cash.CashSessionDto;
import com.essenza.draco.modules.sales.application.dto.cash.CashSessionMethodDto;
import com.essenza.draco.modules.sales.application.output.repository.CashRegisterRepository;
import com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository;
import com.essenza.draco.modules.sales.application.output.repository.StoreRepository;
import com.essenza.draco.modules.sales.application.dto.cash.StoreDto;
import com.essenza.draco.modules.sales.domain.model.cash.RegisterSettings;
import com.essenza.draco.modules.sales.domain.model.cash.Store;
import com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository.MethodCount;
import com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository.MethodTotal;
import com.essenza.draco.modules.sales.domain.model.cash.CashMovement;
import com.essenza.draco.modules.sales.domain.model.cash.CashMovementType;
import com.essenza.draco.modules.sales.domain.model.cash.CashRegister;
import com.essenza.draco.modules.sales.domain.model.cash.CashSession;
import com.essenza.draco.modules.sales.domain.model.cash.CashSessionStatus;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Cálculos y vistas comunes de cajas y turnos. */
@Component
class CashSoporte {

    /** Medios que entran a la gaveta en efectivo. */
    static final Set<String> CASH_KINDS = Set.of("CASH", "CASH_ON_DELIVERY");

    private final CashRegisterRepository registers;
    private final CashSessionRepository sessions;
    private final StoreRepository stores;

    CashSoporte(CashRegisterRepository registers, CashSessionRepository sessions, StoreRepository stores) {
        this.registers = registers;
        this.sessions = sessions;
        this.stores = stores;
    }

    Store store(Long id) {
        return stores.findById(id).orElseThrow(() -> new NotFoundException("No se encontró el punto de venta " + id + "."));
    }

    StoreDto storeDto(Store s) {
        return new StoreDto(s.id(), s.code(), s.name(), s.address(), s.city(), s.phone(), s.email(), s.warehouseId(),
                s.openingHours(), s.ticketHeader(), s.ticketFooter(), s.active(),
                s.id() == null ? 0 : stores.countRegisters(s.id()), s.id() == null ? 0 : stores.countOpenRegisters(s.id()),
                s.countryId(), s.stateId(), s.cityId());
    }

    CashRegister register(Long id) {
        return registers.findById(id).orElseThrow(() -> new NotFoundException("No se encontró la caja " + id + "."));
    }

    CashSession session(Long id) {
        return sessions.findById(id).orElseThrow(() -> new NotFoundException("No se encontró el turno de caja " + id + "."));
    }

    CashSession lockedSession(Long id) {
        return sessions.lockById(id).orElseThrow(() -> new NotFoundException("No se encontró el turno de caja " + id + "."));
    }

    /** Base + efectivo cobrado (aprobado − reembolsado) + entradas − salidas. */
    BigDecimal expectedCash(CashSession session, List<MethodTotal> totals, List<CashMovement> movements) {
        BigDecimal cash = totals.stream().filter(t -> CASH_KINDS.contains(t.kind())).map(MethodTotal::settled)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal moves = movements.stream().map(CashMovement::signedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return session.openingAmount().add(cash).add(moves).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    BigDecimal expectedCash(CashSession session) {
        return expectedCash(session, sessions.methodTotals(session.id()), sessions.movements(session.id()));
    }

    CashSessionDto toDto(CashSession s, boolean full) {
        CashRegister register = registers.findById(s.registerId()).orElse(null);
        List<MethodTotal> totals = sessions.methodTotals(s.id());
        List<CashMovement> movements = sessions.movements(s.id());
        CashSessionRepository.SalesTotals sales = sessions.sales(s.id());
        BigDecimal in = sum(movements, CashMovementType.IN);
        BigDecimal out = sum(movements, CashMovementType.OUT);
        BigDecimal collected = totals.stream().map(MethodTotal::settled).reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean closed = s.status() == CashSessionStatus.CLOSED;
        List<CashSessionMethodDto> methods;
        if (closed) {
            methods = sessions.counts(s.id()).stream()
                    .map(c -> new CashSessionMethodDto(c.paymentMethodId(), c.name(), c.kind(), c.count(), c.expected(),
                            c.counted(), c.difference()))
                    .toList();
        } else {
            methods = totals.stream()
                    .map(t -> new CashSessionMethodDto(t.paymentMethodId(), t.name(), t.kind(), t.count(), t.settled(), null, null))
                    .toList();
        }
        RegisterSettings rules = register == null ? RegisterSettings.defaults() : register.settings();
        Store store = register == null || register.storeId() == null ? null : stores.findById(register.storeId()).orElse(null);
        BigDecimal expectedNow = closed ? s.expectedCash() : expectedCash(s, totals, movements);
        return new CashSessionDto(s.id(), s.registerId(), register == null ? null : register.code(),
                register == null ? null : register.name(), s.status().name(), s.openedBy(), s.openedAt(), s.openingAmount(),
                s.openingNotes(), s.closedBy(), s.closedAt(),
                closed ? s.expectedCash() : expectedCash(s, totals, movements), s.countedCash(), s.cashDifference(),
                s.closingNotes(), sales.count(), sales.total(), collected, in, out, sessions.pendingPayments(s.id()), methods,
                full ? movements.stream().map(CashSoporte::movementDto).toList() : List.of(),
                store == null ? null : store.name(), rules.blindClose(), rules.differenceTolerance(), rules.cashLimit(),
                !closed && rules.overCashLimit(expectedNow), closed && rules.exceedsTolerance(s.cashDifference()));
    }

    CashRegisterDto registerDto(CashRegister r) {
        CashSession open = r.id() == null ? null : sessions.findOpenByRegister(r.id()).orElse(null);
        BigDecimal expected = open == null ? null : expectedCash(open);
        Store store = r.storeId() == null ? null : stores.findById(r.storeId()).orElse(null);
        RegisterSettings c = r.settings();
        return new CashRegisterDto(r.id(), r.code(), r.name(), r.location(), r.warehouseId(), r.active(),
                open == null ? null : open.id(), open == null ? null : open.openedBy(), open == null ? null : open.openedAt(),
                expected, r.id() == null ? 0 : sessions.countByRegister(r.id()),
                r.storeId(), store == null ? null : store.name(),
                r.warehouseId() != null ? r.warehouseId() : store == null ? null : store.warehouseId(),
                c.deviceName(), c.allowedMethodIds(), c.openingMode().name(), c.defaultOpeningAmount(), c.cashLimit(),
                open != null && c.overCashLimit(expected), c.blindClose(), c.differenceTolerance(), c.maxDiscountPercent(),
                c.autoInvoice(), c.receiptWidth(),
                store == null ? null : new CashRegisterDto.TicketInfo(store.name(), store.address(), store.city(),
                        store.phone(), store.ticketHeader(), store.ticketFooter()));
    }

    static CashMovementDto movementDto(CashMovement m) {
        return new CashMovementDto(m.id(), m.sessionId(), m.type().name(), m.amount(), m.reason(), m.reference(),
                m.createdBy(), m.createdAt());
    }

    static MethodCount count(MethodTotal total, BigDecimal counted) {
        BigDecimal c = counted == null ? total.settled() : counted.setScale(2, java.math.RoundingMode.HALF_UP);
        return new MethodCount(total.paymentMethodId(), total.name(), total.kind(), total.count(), total.settled(), c,
                c.subtract(total.settled()));
    }

    private static BigDecimal sum(List<CashMovement> movements, CashMovementType type) {
        return movements.stream().filter(m -> m.type() == type).map(CashMovement::amount)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }
}
