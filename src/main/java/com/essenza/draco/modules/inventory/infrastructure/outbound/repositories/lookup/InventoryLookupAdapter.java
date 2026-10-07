package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.lookup;

import java.util.List;

import org.springframework.stereotype.Component;

import com.essenza.draco.modules.inventory.application.output.repository.InventoryLookupQuery;
import com.essenza.draco.shared.common.lookup.LookupOption;
import com.essenza.draco.shared.common.lookup.LookupRequest;
import com.essenza.draco.shared.common.lookup.NativeLookupSql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Component
public class InventoryLookupAdapter implements InventoryLookupQuery {

    private final EntityManager em;

    public InventoryLookupAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LookupOption> suppliers(LookupRequest request) {
        NativeLookupSql sql = new NativeLookupSql("SELECT s.id_supplier, s.name, s.email FROM suppliers s")
                .where("s.deleted = 0")
                .match(request, "s.id_supplier", "s.name", "s.email")
                .orderBy("s.name");
        Query query = em.createNativeQuery(sql.sql(request.limit()));
        sql.params().forEach(query::setParameter);
        return ((List<Object[]>) query.getResultList()).stream()
                .map(r -> new LookupOption(((Number) r[0]).longValue(), (String) r[1], (String) r[2]))
                .toList();
    }
}
