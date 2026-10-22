package com.essenza.draco.modules.sales.application.services.cash;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.cash.CashMovementDto;
import com.essenza.draco.modules.sales.application.dto.cash.CreateCashMovementDto;
import com.essenza.draco.modules.sales.application.input.cash.AddCashMovementUseCase;
import com.essenza.draco.modules.sales.application.output.repository.CashSessionRepository;
import com.essenza.draco.modules.sales.domain.model.cash.CashMovement;
import com.essenza.draco.modules.sales.domain.model.cash.CashMovementType;
import com.essenza.draco.modules.sales.domain.model.cash.CashRuleViolation;
import com.essenza.draco.modules.sales.domain.model.cash.CashSession;

/** Entradas y salidas de efectivo; una salida no puede superar el efectivo esperado en la caja. */
@Service
@Transactional
public class AddCashMovementService implements AddCashMovementUseCase {

    private final CashSessionRepository sessions;
    private final CashSoporte soporte;

    public AddCashMovementService(CashSessionRepository sessions, CashSoporte soporte) {
        this.sessions = sessions;
        this.soporte = soporte;
    }

    @Override
    public CashMovementDto add(Long sessionId, CreateCashMovementDto input) {
        CashSession session = soporte.lockedSession(sessionId);
        session.requireOpen();
        CashMovement movement = new CashMovement(null, sessionId, CashMovementType.parse(input.type()), input.amount(),
                input.reason(), input.reference(), input.createdBy(), Instant.now());
        if (movement.type() == CashMovementType.OUT) {
            BigDecimal available = soporte.expectedCash(session);
            if (movement.amount().compareTo(available) > 0) {
                throw new CashRuleViolation("La salida (" + movement.amount().toPlainString()
                        + ") supera el efectivo esperado en la caja (" + available.toPlainString() + ").");
            }
        }
        return CashSoporte.movementDto(sessions.saveMovement(movement));
    }
}
