package com.essenza.draco.modules.catalog.application.output.repository;

/** Comprobación de unidades de medida (catálogo de unidades, otro módulo) por id. */
public interface UnitReferences {

    /** La unidad existe (no borrada). */
    boolean exists(Long unitId);
}
