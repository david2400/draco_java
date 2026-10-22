package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.cash;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.sales.application.dto.cash.CashSessionFilter;
import com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository;
import com.essenza.draco.modules.sales.domain.model.cash.CashMovement;
import com.essenza.draco.modules.sales.domain.model.cash.CashMovementType;
import com.essenza.draco.modules.sales.domain.model.cash.CashSession;
import com.essenza.draco.modules.sales.domain.model.cash.CashSessionStatus;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.CashMovementEntity;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.CashSessionCountEntity;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.CashSessionEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;

@Repository
public class CashSessionRepositoryAdapter implements CashSessionRepository {

    private final JpaCashSessionRepository jpa;
    private final JpaCashMovementRepository movementsJpa;
    private final JpaCashSessionCountRepository countsJpa;

    @PersistenceContext
    private EntityManager em;

    public CashSessionRepositoryAdapter(JpaCashSessionRepository jpa, JpaCashMovementRepository movementsJpa,
                                        JpaCashSessionCountRepository countsJpa) {
        this.jpa = jpa;
        this.movementsJpa = movementsJpa;
        this.countsJpa = countsJpa;
    }

    @Override
    public CashSession save(CashSession s) {
        CashSessionEntity e = s.id() == null ? new CashSessionEntity()
                : jpa.findById(s.id()).orElseThrow(() -> new NotFoundException("Turno no encontrado: " + s.id()));
        e.setCashRegisterId(s.registerId());
        e.setStatus(s.status().name());
        e.setOpenedBy(s.openedBy());
        e.setOpenedAt(s.openedAt());
        e.setOpeningAmount(s.openingAmount());
        e.setOpeningNotes(s.openingNotes());
        e.setClosedBy(s.closedBy());
        e.setClosedAt(s.closedAt());
        e.setExpectedCash(s.expectedCash());
        e.setCountedCash(s.countedCash());
        e.setCashDifference(s.cashDifference());
        e.setClosingNotes(s.closingNotes());
        return toDomain(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<CashSession> findById(Long id) {
        return jpa.findById(id).map(CashSessionRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<CashSession> lockById(Long id) {
        return jpa.lockById(id).map(CashSessionRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<CashSession> findOpenByRegister(Long registerId) {
        return jpa.findByCashRegisterIdAndStatus(registerId, CashSessionStatus.OPEN.name()).stream().findFirst()
                .map(CashSessionRepositoryAdapter::toDomain);
    }

    @Override
    public Page<CashSession> search(CashSessionFilter f, Pageable pageable) {
        Specification<CashSessionEntity> spec = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (f != null && f.cashRegisterId() != null) {
                list.add(cb.equal(root.get("cashRegisterId"), f.cashRegisterId()));
            }
            if (f != null && f.status() != null && !f.status().isBlank()) {
                list.add(cb.equal(root.get("status"), f.status().trim().toUpperCase(Locale.ROOT)));
            }
            if (f != null && f.from() != null) {
                list.add(cb.greaterThanOrEqualTo(root.<Instant>get("openedAt"), f.from()));
            }
            if (f != null && f.to() != null) {
                list.add(cb.lessThan(root.<Instant>get("openedAt"), f.to()));
            }
            return cb.and(list.toArray(Predicate[]::new));
        };
        return jpa.findAll(spec, pageable).map(CashSessionRepositoryAdapter::toDomain);
    }

    @Override
    public long countByRegister(Long registerId) {
        return jpa.countByCashRegisterId(registerId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public java.util.Set<Long> allowedMethodIds(Long sessionId) {
        List<Object> rows = em.createNativeQuery("""
                        SELECT m.payment_method_id FROM cash_register_methods m
                          JOIN cash_sessions s ON s.cash_register_id = m.cash_register_id
                         WHERE s.id_cash_session = :id""")
                .setParameter("id", sessionId).getResultList();
        return rows.stream().map(v -> ((Number) v).longValue()).collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public Long registerIdOf(Long sessionId) {
        return jpa.findById(sessionId).map(e -> e.getCashRegisterId()).orElse(null);
    }

    @Override
    public CashMovement saveMovement(CashMovement m) {
        CashMovementEntity e = new CashMovementEntity();
        e.setCashSessionId(m.sessionId());
        e.setType(m.type().name());
        e.setAmount(m.amount());
        e.setReason(m.reason());
        e.setReference(m.reference());
        e.setCreatedBy(m.createdBy());
        e.setCreatedAt(m.createdAt());
        return toMovement(movementsJpa.saveAndFlush(e));
    }

    @Override
    public List<CashMovement> movements(Long sessionId) {
        return movementsJpa.findByCashSessionIdOrderByIdAsc(sessionId).stream().map(CashSessionRepositoryAdapter::toMovement).toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MethodTotal> methodTotals(Long sessionId) {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT p.payment_type_id, t.name, p.kind, COUNT(*), COALESCE(SUM(p.amount - p.refunded_amount), 0)
                  FROM payments p
                  JOIN payment_types t ON t.id_payment_type = p.payment_type_id
                 WHERE p.cash_session_id = :id AND p.status = 'APPROVED' AND p.deleted = 0
                 GROUP BY p.payment_type_id, t.name, p.kind
                 ORDER BY t.name""").setParameter("id", sessionId).getResultList();
        return rows.stream().map(r -> new MethodTotal(((Number) r[0]).longValue(), (String) r[1], (String) r[2],
                ((Number) r[3]).longValue(), money(r[4]))).toList();
    }

    @Override
    public long pendingPayments(Long sessionId) {
        Object count = em.createNativeQuery(
                        "SELECT COUNT(*) FROM payments WHERE cash_session_id = :id AND status = 'PENDING' AND deleted = 0")
                .setParameter("id", sessionId).getSingleResult();
        return ((Number) count).longValue();
    }

    @Override
    public SalesTotals sales(Long sessionId) {
        Object[] row = (Object[]) em.createNativeQuery("""
                SELECT COUNT(*), COALESCE(SUM(total), 0) FROM orders
                 WHERE cash_session_id = :id AND state <> 'CANCELLED' AND deleted = 0""")
                .setParameter("id", sessionId).getSingleResult();
        return new SalesTotals(((Number) row[0]).longValue(), money(row[1]));
    }

    @Override
    public void saveCounts(Long sessionId, List<MethodCount> counts) {
        for (MethodCount c : counts) {
            CashSessionCountEntity e = new CashSessionCountEntity();
            e.setCashSessionId(sessionId);
            e.setPaymentMethodId(c.paymentMethodId());
            e.setMethodName(c.name());
            e.setKind(c.kind());
            e.setPaymentsCount((int) c.count());
            e.setExpectedAmount(c.expected());
            e.setCountedAmount(c.counted());
            e.setDifference(c.difference());
            countsJpa.save(e);
        }
        countsJpa.flush();
    }

    @Override
    public List<MethodCount> counts(Long sessionId) {
        return countsJpa.findByCashSessionIdOrderByIdAsc(sessionId).stream()
                .map(e -> new MethodCount(e.getPaymentMethodId(), e.getMethodName(), e.getKind(),
                        e.getPaymentsCount() == null ? 0 : e.getPaymentsCount(), e.getExpectedAmount(), e.getCountedAmount(),
                        e.getDifference()))
                .toList();
    }

    static CashSession toDomain(CashSessionEntity e) {
        return new CashSession(e.getId(), e.getCashRegisterId(), CashSessionStatus.valueOf(e.getStatus()), e.getOpenedBy(),
                e.getOpenedAt(), e.getOpeningAmount(), e.getOpeningNotes(), e.getClosedBy(), e.getClosedAt(),
                e.getExpectedCash(), e.getCountedCash(), e.getCashDifference(), e.getClosingNotes());
    }

    static CashMovement toMovement(CashMovementEntity e) {
        return new CashMovement(e.getId(), e.getCashSessionId(), CashMovementType.valueOf(e.getType()), e.getAmount(),
                e.getReason(), e.getReference(), e.getCreatedBy(), e.getCreatedAt());
    }

    private static BigDecimal money(Object value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2);
        }
        return (value instanceof BigDecimal d ? d : new BigDecimal(value.toString())).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
