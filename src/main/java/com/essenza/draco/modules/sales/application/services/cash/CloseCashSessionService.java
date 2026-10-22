package com.essenza.draco.modules.sales.application.services.cash;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.cash.CashSessionDto;
import com.essenza.draco.modules.sales.application.dto.cash.CloseCashSessionDto;
import com.essenza.draco.modules.sales.application.input.cash.CloseCashSessionUseCase;
import com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository;
import com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository.MethodCount;
import com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository.MethodTotal;
import com.essenza.draco.modules.sales.domain.model.cash.CashRuleViolation;
import com.essenza.draco.modules.sales.domain.model.cash.CashSession;

/**
 * Cierre con arqueo: el efectivo esperado (base + cobrado en efectivo + entradas − salidas)
 * contra el contado; los demás medios se concilian con su conteo (vouchers, extractos).
 * Los valores quedan congelados en el turno.
 */
@Service
@Transactional
public class CloseCashSessionService implements CloseCashSessionUseCase {

    private final CashSessionRepository sessions;
    private final CashSoporte soporte;

    public CloseCashSessionService(CashSessionRepository sessions, CashSoporte soporte) {
        this.sessions = sessions;
        this.soporte = soporte;
    }

    @Override
    public CashSessionDto close(Long sessionId, CloseCashSessionDto input) {
        CashSession session = soporte.lockedSession(sessionId);
        session.requireOpen();
        List<MethodTotal> totals = sessions.methodTotals(sessionId);
        BigDecimal expected = soporte.expectedCash(session, totals, sessions.movements(sessionId));
        Map<Long, BigDecimal> counted = new HashMap<>();
        if (input.counts() != null) {
            input.counts().forEach(c -> counted.put(c.paymentMethodId(), c.countedAmount()));
        }
        // El efectivo se arquea en el total del turno; los demás medios, uno por uno.
        List<MethodCount> counts = totals.stream()
                .map(t -> CashSoporte.count(t, CashSoporte.CASH_KINDS.contains(t.kind()) ? null : counted.get(t.paymentMethodId())))
                .toList();
        var settings = soporte.register(session.registerId()).settings();
        java.math.BigDecimal difference = input.countedCash().setScale(2, java.math.RoundingMode.HALF_UP).subtract(expected);
        if (settings.exceedsTolerance(difference) && (input.notes() == null || input.notes().isBlank())) {
            throw new CashRuleViolation("La diferencia del arqueo (" + difference.toPlainString() + ") supera la tolerancia de la caja ("
                    + settings.differenceTolerance().toPlainString() + "): explica la diferencia en las notas de cierre.");
        }
        session.close(input.closedBy(), expected, input.countedCash(), input.notes(), Instant.now());
        CashSession saved = sessions.save(session);
        sessions.saveCounts(sessionId, counts);
        return soporte.toDto(saved, true);
    }
}
