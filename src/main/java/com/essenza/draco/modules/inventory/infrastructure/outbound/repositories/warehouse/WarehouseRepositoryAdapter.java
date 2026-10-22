package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.warehouse;

import com.essenza.draco.modules.inventory.application.output.repository.WarehouseFilter;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseRepository;
import com.essenza.draco.modules.inventory.domain.model.warehouse.Warehouse;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseCapacity;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseOperation;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseType;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.WarehouseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Bodegas en {@code warehouses}. */
@Repository
public class WarehouseRepositoryAdapter implements WarehouseRepository {

    private final JpaWarehouseRepository jpa;
    private final EntityManager em;

    public WarehouseRepositoryAdapter(JpaWarehouseRepository jpa, EntityManager em) {
        this.jpa = jpa;
        this.em = em;
    }

    @Override
    public Optional<Warehouse> findById(Long id) {
        return jpa.findById(id).map(WarehouseRepositoryAdapter::toDomain);
    }

    @Override
    public Warehouse save(Warehouse w) {
        WarehouseEntity e = w.id() == null ? new WarehouseEntity() : jpa.findById(w.id()).orElseGet(WarehouseEntity::new);
        e.setCode(w.code());
        e.setName(w.name());
        e.setWarehouseType(w.type().name());
        e.setIsMain(w.main());
        e.setActive(w.active());
        e.setAllowSales(w.operation().sales());
        e.setAllowOnline(w.operation().online());
        e.setAllowReceiving(w.operation().receiving());
        e.setFulfillmentPriority(w.operation().priority());
        e.setAddress(w.address());
        e.setCountryId(w.countryId());
        e.setStateId(w.stateId());
        e.setCityId(w.cityId());
        e.setCity(w.city());
        e.setManagerName(w.managerName());
        e.setManagerPhone(w.managerPhone());
        e.setManagerEmail(w.managerEmail());
        e.setOpeningHours(w.openingHours());
        e.setNotes(w.notes());
        e.setAreaM2(w.capacity().areaM2());
        e.setCapacityUnits(w.capacity().units());
        e.setCapacityPositions(w.capacity().positions());
        return toDomain(jpa.saveAndFlush(e));
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public Page<Warehouse> search(WarehouseFilter f, Pageable pageable) {
        Specification<WarehouseEntity> spec = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (f != null && f.q() != null && !f.q().isBlank()) {
                String like = "%" + f.q().trim().toLowerCase(Locale.ROOT) + "%";
                list.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("code")), like),
                        cb.like(cb.lower(root.get("city")), like),
                        cb.like(cb.lower(root.get("address")), like),
                        cb.like(cb.lower(root.get("managerName")), like)));
            }
            if (f != null && f.type() != null && !f.type().isBlank()) {
                list.add(cb.equal(root.get("warehouseType"), WarehouseType.of(f.type()).name()));
            }
            if (f != null && f.active() != null) {
                list.add(cb.equal(root.get("active"), f.active()));
            }
            if (f != null && f.cityId() != null) {
                list.add(cb.equal(root.get("cityId"), f.cityId()));
            }
            return cb.and(list.toArray(Predicate[]::new));
        };
        return jpa.findAll(spec, pageable).map(WarehouseRepositoryAdapter::toDomain);
    }

    @Override
    public boolean existsCode(String code, Long excludeId) {
        return excludeId == null ? jpa.existsByCode(code) : jpa.existsByCodeAndIdNot(code, excludeId);
    }

    @Override
    public long nextNumber() {
        // Incluye las borradas: el código automático no se reutiliza.
        Number max = (Number) em.createNativeQuery("SELECT COALESCE(MAX(id_warehouse), 0) FROM warehouses").getSingleResult();
        long next = max.longValue() + 1;
        while (jpa.existsByCode("BOD_" + next)) {
            next++;
        }
        return next;
    }

    @Override
    public void clearMain(Long exceptId) {
        jpa.clearMain(exceptId);
    }

    @Override
    public Optional<Warehouse> findMain() {
        return jpa.findFirstByIsMainTrueOrderByIdAsc().map(WarehouseRepositoryAdapter::toDomain);
    }

    /**
     * Lectura tolerante de registros anteriores a V14: código saneado (BOD_<id> si queda vacío), principal
     * solo si está activa y es de un tipo que lo permite, y tránsito sin venta.
     */
    static Warehouse toDomain(WarehouseEntity e) {
        String code = e.getCode() == null ? "" : e.getCode().trim().toUpperCase(Locale.ROOT)
                .replaceAll("\\s+", "_").replaceAll("[^A-Z0-9_.-]", "");
        if (code.isEmpty() || !Character.isLetterOrDigit(code.charAt(0))) {
            code = "BOD_" + e.getId();
        }
        if (code.length() > 50) {
            code = code.substring(0, 50);
        }
        WarehouseType type = WarehouseType.of(e.getWarehouseType());
        boolean active = !Boolean.FALSE.equals(e.getActive());
        boolean main = Boolean.TRUE.equals(e.getIsMain()) && active && type.canBeMain();
        int priority = e.getFulfillmentPriority() == null ? 10 : Math.max(1, Math.min(99, e.getFulfillmentPriority()));
        WarehouseOperation op = new WarehouseOperation(!Boolean.FALSE.equals(e.getAllowSales()) && type != WarehouseType.TRANSITO,
                !Boolean.FALSE.equals(e.getAllowOnline()), !Boolean.FALSE.equals(e.getAllowReceiving()), priority);
        String email = e.getManagerEmail() != null && e.getManagerEmail().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")
                ? e.getManagerEmail() : null;
        Long stateId = e.getCityId() != null && e.getStateId() == null ? null : e.getStateId();
        Long cityId = stateId == null ? null : e.getCityId();
        Long countryId = e.getCountryId();
        if (stateId != null && countryId == null) {
            stateId = null;
            cityId = null;
        }
        WarehouseCapacity cap = new WarehouseCapacity(
                e.getAreaM2() != null && e.getAreaM2().signum() > 0 ? e.getAreaM2() : null,
                e.getCapacityUnits() != null && e.getCapacityUnits() > 0 ? e.getCapacityUnits() : null,
                e.getCapacityPositions() != null && e.getCapacityPositions() > 0 ? e.getCapacityPositions() : null);
        return new Warehouse(e.getId(), code, e.getName() == null || e.getName().isBlank() ? code : e.getName(), type, main,
                active, op, e.getAddress(), countryId, stateId, cityId, e.getCity(), e.getManagerName(), e.getManagerPhone(),
                email, e.getOpeningHours(), e.getNotes(), cap);
    }
}
