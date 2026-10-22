package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.supplier;

import com.essenza.draco.modules.inventory.application.output.repository.SupplierFilter;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierRepository;
import com.essenza.draco.modules.inventory.domain.model.supplier.BankAccount;
import com.essenza.draco.modules.inventory.domain.model.supplier.CommercialTerms;
import com.essenza.draco.modules.inventory.domain.model.supplier.ContactArea;
import com.essenza.draco.modules.inventory.domain.model.supplier.PersonType;
import com.essenza.draco.modules.inventory.domain.model.supplier.Supplier;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierContact;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierStatus;
import com.essenza.draco.modules.inventory.domain.model.supplier.TaxId;
import com.essenza.draco.modules.inventory.domain.model.supplier.TaxRegime;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.SupplierContactEntity;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.SupplierEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Ficha del proveedor en {@code suppliers} y sus contactos en {@code supplier_contacts}. */
@Repository
public class SupplierRepositoryAdapter implements SupplierRepository {

    private final JpaSupplierRepository jpa;
    private final JpaSupplierContactRepository contacts;
    private final EntityManager em;

    public SupplierRepositoryAdapter(JpaSupplierRepository jpa, JpaSupplierContactRepository contacts, EntityManager em) {
        this.jpa = jpa;
        this.contacts = contacts;
        this.em = em;
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return jpa.findById(id).map(e -> toDomain(e, contactsOf(List.of(e.getId())).getOrDefault(e.getId(), List.of())));
    }

    @Override
    public Supplier save(Supplier s) {
        SupplierEntity e = s.id() == null ? new SupplierEntity() : jpa.findById(s.id()).orElseGet(SupplierEntity::new);
        e.setCode(s.code());
        e.setDocType(s.taxId() == null ? null : s.taxId().type());
        e.setDocNumber(s.taxId() == null ? null : s.taxId().number());
        e.setDv(s.taxId() == null ? null : s.taxId().dv());
        e.setPersonType(s.personType().name());
        e.setName(s.name());
        e.setTradeName(s.tradeName());
        e.setTaxRegime(s.taxRegime() == null ? null : s.taxRegime().name());
        e.setGreatContributor(s.greatContributor());
        e.setSelfRetainer(s.selfRetainer());
        e.setEmail(s.email());
        e.setPhone(s.phone());
        e.setWebsite(s.website());
        e.setAddress(s.address());
        e.setCountryId(s.countryId());
        e.setStateId(s.stateId());
        e.setCityId(s.cityId());
        e.setCity(s.city());
        e.setStatus(s.status().name());
        e.setBlockReason(s.blockReason());
        CommercialTerms t = s.terms();
        e.setPaymentTermDays(t.paymentTermDays());
        e.setCreditLimit(t.creditLimit());
        e.setCurrency(t.currency());
        e.setLeadTimeDays(t.leadTimeDays());
        e.setMinOrderAmount(t.minOrderAmount());
        e.setDiscountPercent(t.discountPercent());
        BankAccount b = s.bank();
        e.setBankName(b == null ? null : b.bankName());
        e.setBankAccountType(b == null ? null : b.accountType());
        e.setBankAccountNumber(b == null ? null : b.accountNumber());
        e.setBankAccountHolder(b == null ? null : b.holderName());
        e.setBankHolderDocument(b == null ? null : b.holderDocument());
        e.setNotes(s.notes());
        SupplierEntity saved = jpa.saveAndFlush(e);

        contacts.deleteBySupplier(saved.getId());
        List<SupplierContactEntity> rows = new ArrayList<>();
        for (int i = 0; i < s.contacts().size(); i++) {
            SupplierContact c = s.contacts().get(i);
            SupplierContactEntity row = new SupplierContactEntity();
            row.setSupplierId(saved.getId());
            row.setName(c.name());
            row.setPosition(c.position());
            row.setArea(c.area().name());
            row.setPhone(c.phone());
            row.setEmail(c.email());
            row.setPrimary(c.primary());
            row.setSortOrder(i);
            rows.add(row);
        }
        List<SupplierContact> stored = contacts.saveAll(rows).stream().map(SupplierRepositoryAdapter::toContact).toList();
        return toDomain(saved, stored);
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public Page<Supplier> search(SupplierFilter f, Pageable pageable) {
        Specification<SupplierEntity> spec = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (f != null && f.q() != null && !f.q().isBlank()) {
                String like = "%" + f.q().trim().toLowerCase(Locale.ROOT) + "%";
                String digits = f.q().replaceAll("[^0-9]", "");
                List<Predicate> any = new ArrayList<>(List.of(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("tradeName")), like),
                        cb.like(cb.lower(root.get("code")), like),
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("city")), like),
                        cb.like(cb.lower(root.get("docNumber")), like)));
                if (digits.length() >= 3) {
                    // "900.123.456-8" encuentra el NIT 900123456.
                    any.add(cb.like(root.get("docNumber"), "%" + (digits.length() > 9 ? digits.substring(0, 9) : digits) + "%"));
                }
                list.add(cb.or(any.toArray(Predicate[]::new)));
            }
            if (f != null && f.status() != null && !f.status().isBlank()) {
                list.add(cb.equal(root.get("status"), SupplierStatus.of(f.status()).name()));
            }
            if (f != null && f.cityId() != null) {
                list.add(cb.equal(root.get("cityId"), f.cityId()));
            }
            return cb.and(list.toArray(Predicate[]::new));
        };
        Page<SupplierEntity> page = jpa.findAll(spec, pageable);
        Map<Long, List<SupplierContact>> byId = contactsOf(page.getContent().stream().map(SupplierEntity::getId).toList());
        List<Supplier> content = page.getContent().stream()
                .map(e -> toDomain(e, byId.getOrDefault(e.getId(), List.of()))).toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public List<Supplier> findAll() {
        List<SupplierEntity> all = jpa.findAll(Sort.by("name"));
        Map<Long, List<SupplierContact>> byId = contactsOf(all.stream().map(SupplierEntity::getId).toList());
        return all.stream().map(e -> toDomain(e, byId.getOrDefault(e.getId(), List.of()))).toList();
    }

    @Override
    public boolean existsCode(String code, Long excludeId) {
        return excludeId == null ? jpa.existsByCode(code) : jpa.existsByCodeAndIdNot(code, excludeId);
    }

    @Override
    public boolean existsDocument(String docType, String docNumber, Long excludeId) {
        return excludeId == null ? jpa.existsByDocTypeAndDocNumber(docType, docNumber)
                : jpa.existsByDocTypeAndDocNumberAndIdNot(docType, docNumber, excludeId);
    }

    @Override
    public long nextNumber() {
        // Incluye los borrados: el código PROV_<n> no se reutiliza.
        Number max = (Number) em.createNativeQuery("SELECT COALESCE(MAX(id_supplier), 0) FROM suppliers").getSingleResult();
        long next = max.longValue() + 1;
        while (jpa.existsByCode("PROV_" + next)) {
            next++;
        }
        return next;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Long> countProducts(Collection<Long> supplierIds) {
        if (supplierIds == null || supplierIds.isEmpty()) {
            return Map.of();
        }
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT supplier_id, COUNT(*) FROM supplier_products
                         WHERE active = 1 AND supplier_id IN (:ids) GROUP BY supplier_id""")
                .setParameter("ids", supplierIds).getResultList();
        Map<Long, Long> out = new HashMap<>();
        rows.forEach(r -> out.put(((Number) r[0]).longValue(), ((Number) r[1]).longValue()));
        return out;
    }

    @Override
    public long countCatalogProducts(Long supplierId) {
        Number n = (Number) em.createNativeQuery("SELECT COUNT(*) FROM products WHERE deleted = 0 AND supplier_id = :id")
                .setParameter("id", supplierId).getSingleResult();
        return n.longValue();
    }

    @Override
    public Counts counts() {
        Object[] r = (Object[]) em.createNativeQuery("""
                SELECT COUNT(*),
                       COALESCE(SUM(status = 'ACTIVE'), 0),
                       COALESCE(SUM(status = 'BLOCKED'), 0),
                       COALESCE(SUM(status = 'INACTIVE'), 0),
                       COALESCE(SUM(doc_number IS NULL), 0),
                       COALESCE(SUM(payment_term_days > 0), 0)
                  FROM suppliers WHERE deleted = 0""").getSingleResult();
        return new Counts(n(r[0]), n(r[1]), n(r[2]), n(r[3]), n(r[4]), n(r[5]));
    }

    private Map<Long, List<SupplierContact>> contactsOf(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return contacts.findBySupplierIdInOrderBySupplierIdAscSortOrderAsc(ids).stream()
                .collect(Collectors.groupingBy(SupplierContactEntity::getSupplierId,
                        Collectors.mapping(SupplierRepositoryAdapter::toContact, Collectors.toList())));
    }

    private static long n(Object value) {
        return value == null ? 0 : ((Number) value).longValue();
    }

    private static SupplierContact toContact(SupplierContactEntity c) {
        return new SupplierContact(c.getId(), c.getName(), c.getPosition(), ContactArea.of(c.getArea()), c.getPhone(),
                c.getEmail(), Boolean.TRUE.equals(c.getPrimary()));
    }

    /**
     * Los registros anteriores a V13 pueden traer datos que hoy no pasan la validación (correo mal escrito,
     * contacto sin teléfono): se leen tal cual con los valores mínimos para no romper el listado.
     */
    static Supplier toDomain(SupplierEntity e, List<SupplierContact> contacts) {
        TaxId tax = e.getDocType() == null || e.getDocNumber() == null ? null
                : new TaxId(e.getDocType(), e.getDocNumber(), e.getDv());
        BankAccount bank = e.getBankName() == null || e.getBankAccountType() == null || e.getBankAccountNumber() == null
                ? null : new BankAccount(e.getBankName(), e.getBankAccountType(), e.getBankAccountNumber(),
                e.getBankAccountHolder(), e.getBankHolderDocument());
        CommercialTerms terms = new CommercialTerms(e.getPaymentTermDays() == null ? 0 : e.getPaymentTermDays(),
                e.getCreditLimit(), e.getCurrency(), e.getLeadTimeDays(), e.getMinOrderAmount(), e.getDiscountPercent());
        SupplierStatus status = SupplierStatus.of(e.getStatus());
        String email = e.getEmail() != null && e.getEmail().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+") ? e.getEmail() : null;
        return new Supplier(e.getId(), e.getCode() == null ? "PROV_" + e.getId() : e.getCode(), tax,
                PersonType.of(e.getPersonType()), e.getName(), e.getTradeName(), TaxRegime.of(e.getTaxRegime()),
                Boolean.TRUE.equals(e.getGreatContributor()), Boolean.TRUE.equals(e.getSelfRetainer()), email, e.getPhone(),
                e.getWebsite(), e.getAddress(), e.getCountryId(), e.getStateId(), e.getCityId(), e.getCity(), status,
                status == SupplierStatus.BLOCKED && e.getBlockReason() == null ? "—" : e.getBlockReason(), terms, bank,
                e.getNotes(), contacts);
    }
}
