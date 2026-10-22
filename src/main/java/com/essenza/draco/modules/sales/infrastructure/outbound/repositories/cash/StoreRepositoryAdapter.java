package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.cash;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.sales.application.output.repository.StoreRepository;
import com.essenza.draco.modules.sales.domain.model.cash.Store;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.StoreEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Repository
public class StoreRepositoryAdapter implements StoreRepository {

    private final JpaStoreRepository jpa;

    @PersistenceContext
    private EntityManager em;

    public StoreRepositoryAdapter(JpaStoreRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Store> findAll() {
        return jpa.findAll().stream().sorted(Comparator.comparing(StoreEntity::getName, String.CASE_INSENSITIVE_ORDER))
                .map(StoreRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<Store> findById(Long id) {
        return jpa.findById(id).map(StoreRepositoryAdapter::toDomain);
    }

    @Override
    public Store save(Store s) {
        StoreEntity e = s.id() == null ? new StoreEntity()
                : jpa.findById(s.id()).orElseThrow(() -> new NotFoundException("Punto de venta no encontrado: " + s.id()));
        e.setCode(s.code());
        e.setName(s.name());
        e.setAddress(s.address());
        e.setCity(s.city());
        e.setCountryId(s.countryId());
        e.setStateId(s.stateId());
        e.setCityId(s.cityId());
        e.setPhone(s.phone());
        e.setEmail(s.email());
        e.setWarehouseId(s.warehouseId());
        e.setOpeningHours(s.openingHours());
        e.setTicketHeader(s.ticketHeader());
        e.setTicketFooter(s.ticketFooter());
        e.setActive(s.active());
        return toDomain(jpa.saveAndFlush(e));
    }

    @Override
    public void delete(Long id) {
        jpa.findById(id).ifPresent(e -> {
            jpa.delete(e);
            jpa.flush();
        });
    }

    @Override
    public boolean existsCode(String code, Long excludeId) {
        return jpa.existsCode(code, excludeId);
    }

    @Override
    public long countRegisters(Long storeId) {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM cash_registers WHERE store_id = :id AND deleted = 0")
                .setParameter("id", storeId).getSingleResult()).longValue();
    }

    @Override
    public long countOpenRegisters(Long storeId) {
        return ((Number) em.createNativeQuery("""
                        SELECT COUNT(DISTINCT r.id_cash_register) FROM cash_registers r
                          JOIN cash_sessions s ON s.cash_register_id = r.id_cash_register AND s.status = 'OPEN'
                         WHERE r.store_id = :id AND r.deleted = 0""")
                .setParameter("id", storeId).getSingleResult()).longValue();
    }

    @Override
    public boolean warehouseExists(Long warehouseId) {
        // Ventas no importa clases de inventario: consulta directa a la tabla.
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM warehouses WHERE id_warehouse = :id AND deleted = 0")
                .setParameter("id", warehouseId).getSingleResult()).longValue() > 0;
    }

    static Store toDomain(StoreEntity e) {
        return new Store(e.getId(), e.getCode(), e.getName(), e.getAddress(), e.getCity(), e.getPhone(), e.getEmail(),
                e.getWarehouseId(), e.getOpeningHours(), e.getTicketHeader(), e.getTicketFooter(),
                !Boolean.FALSE.equals(e.getActive()), e.getCountryId(), e.getStateId(), e.getCityId());
    }
}
