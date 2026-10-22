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
        // Nombre comercial (o razón social) y documento; solo proveedores activos salvo al pedir ids concretos
        // (un producto puede tener asignado un proveedor ya bloqueado o inactivo).
        NativeLookupSql sql = new NativeLookupSql("""
                SELECT s.id_supplier, COALESCE(s.trade_name, s.name),
                       COALESCE(CONCAT(s.doc_type, ' ', s.doc_number, IF(s.dv IS NULL, '', CONCAT('-', s.dv))), s.email)
                  FROM suppliers s""")
                .where("s.deleted = 0");
        if (!request.byIds()) {
            sql.where("s.status = 'ACTIVE'");
        }
        sql.match(request, "s.id_supplier", "s.name", "s.trade_name", "s.doc_number", "s.email")
                .orderBy("COALESCE(s.trade_name, s.name)");
        Query query = em.createNativeQuery(sql.sql(request.limit()));
        sql.params().forEach(query::setParameter);
        return ((List<Object[]>) query.getResultList()).stream()
                .map(r -> new LookupOption(((Number) r[0]).longValue(), (String) r[1], (String) r[2]))
                .toList();
    }
}
