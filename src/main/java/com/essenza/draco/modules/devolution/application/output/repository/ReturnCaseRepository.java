package com.essenza.draco.modules.devolution.application.output.repository;

import java.util.Map;
import java.util.Optional;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;

/** Persistencia del flujo de devoluciones (cabecera + líneas). */
public interface ReturnCaseRepository {

    Optional<ReturnCase> find(Long id);

    /** Carga la devolución bloqueándola (serializa las acciones del flujo). */
    Optional<ReturnCase> lock(Long id);

    /** Crea (id nulo) o actualiza la cabecera y las líneas existentes. */
    ReturnCase save(ReturnCase returnCase);

    /**
     * Unidades ya devueltas de cada línea de la orden en devoluciones no rechazadas
     * (recibidas si ya llegaron; solicitadas si no), sin contar {@code excludeDevolutionId}.
     */
    Map<Long, Integer> returnedUnits(Long orderId, Long excludeDevolutionId);
}
