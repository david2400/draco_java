package com.essenza.draco.modules.sales.application.output.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.essenza.draco.modules.sales.application.dto.cash.CashSessionFilter;
import com.essenza.draco.modules.sales.domain.model.cash.CashMovement;
import com.essenza.draco.modules.sales.domain.model.cash.CashSession;

/** Turnos de caja con sus movimientos, arqueo y los totales cobrados en el turno. */
public interface CashSessionRepository {

    CashSession save(CashSession session);

    Optional<CashSession> findById(Long id);

    /** Bloquea el turno (serializa cierre, movimientos y cobros). */
    Optional<CashSession> lockById(Long id);

    Optional<CashSession> findOpenByRegister(Long registerId);

    Page<CashSession> search(CashSessionFilter filter, Pageable pageable);

    long countByRegister(Long registerId);

    /** Medios de pago permitidos en la caja del turno (vacío = todos). */
    java.util.Set<Long> allowedMethodIds(Long sessionId);

    /** Caja del turno. */
    Long registerIdOf(Long sessionId);

    CashMovement saveMovement(CashMovement movement);

    List<CashMovement> movements(Long sessionId);

    /** Pagos aprobados del turno agrupados por medio (aprobado − reembolsado). */
    List<MethodTotal> methodTotals(Long sessionId);

    long pendingPayments(Long sessionId);

    SalesTotals sales(Long sessionId);

    void saveCounts(Long sessionId, List<MethodCount> counts);

    List<MethodCount> counts(Long sessionId);

    record MethodTotal(Long paymentMethodId, String name, String kind, long count, BigDecimal settled) {
    }

    record MethodCount(Long paymentMethodId, String name, String kind, long count, BigDecimal expected, BigDecimal counted,
                       BigDecimal difference) {
    }

    record SalesTotals(long count, BigDecimal total) {
    }
}
