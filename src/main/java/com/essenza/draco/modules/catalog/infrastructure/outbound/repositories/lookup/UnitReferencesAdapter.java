package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.lookup;

import org.springframework.stereotype.Component;

import com.essenza.draco.modules.catalog.application.output.repository.UnitReferences;

import jakarta.persistence.EntityManager;

@Component
public class UnitReferencesAdapter implements UnitReferences {

    private final EntityManager em;

    public UnitReferencesAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean exists(Long unitId) {
        if (unitId == null) {
            return false;
        }
        Number count = (Number) em.createNativeQuery(
                        "SELECT COUNT(*) FROM units_measurement WHERE id_unit_measurement = :id AND deleted = 0")
                .setParameter("id", unitId).getSingleResult();
        return count.longValue() > 0;
    }
}
