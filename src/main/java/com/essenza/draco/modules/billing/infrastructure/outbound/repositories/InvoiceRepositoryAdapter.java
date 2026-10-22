package com.essenza.draco.modules.billing.infrastructure.outbound.repositories;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceFilter;
import com.essenza.draco.modules.billing.application.output.repository.InvoiceRepository;
import com.essenza.draco.modules.billing.domain.model.BillingEnvironment;
import com.essenza.draco.modules.billing.domain.model.Buyer;
import com.essenza.draco.modules.billing.domain.model.DocumentKind;
import com.essenza.draco.modules.billing.domain.model.Invoice;
import com.essenza.draco.modules.billing.domain.model.InvoiceLine;
import com.essenza.draco.modules.billing.domain.model.InvoiceStatus;
import com.essenza.draco.modules.billing.infrastructure.outbound.persistence.InvoiceEntity;
import com.essenza.draco.modules.billing.infrastructure.outbound.persistence.InvoiceLineEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final JpaInvoiceRepository jpa;
    private final JpaInvoiceLineRepository lineJpa;

    @PersistenceContext
    private EntityManager em;

    public InvoiceRepositoryAdapter(JpaInvoiceRepository jpa, JpaInvoiceLineRepository lineJpa) {
        this.jpa = jpa;
        this.lineJpa = lineJpa;
    }

    @Override
    public Invoice save(Invoice invoice) {
        boolean isNew = invoice.id() == null;
        InvoiceEntity e = isNew ? new InvoiceEntity()
                : jpa.findById(invoice.id()).orElseThrow(() -> new IllegalStateException("Documento no encontrado: " + invoice.id()));
        e.setKind(invoice.kind().name());
        e.setOrderId(invoice.orderId());
        e.setRelatedInvoiceId(invoice.relatedInvoiceId());
        e.setResolutionId(invoice.resolutionId());
        e.setPrefix(invoice.prefix());
        e.setNumber(invoice.number());
        e.setFullNumber(invoice.fullNumber());
        e.setIssuedAt(invoice.issuedAt());
        e.setStatus(invoice.status().name());
        e.setEnvironment(invoice.environment().name());
        Buyer b = invoice.buyer();
        e.setBuyerDocType(b.docType());
        e.setBuyerDocNumber(b.docNumber());
        e.setBuyerDv(b.dv());
        e.setBuyerName(b.name());
        e.setBuyerEmail(b.email());
        e.setBuyerPhone(b.phone());
        e.setBuyerAddress(b.address());
        e.setBuyerCity(b.city());
        e.setNetAmount(invoice.netAmount());
        e.setTaxAmount(invoice.taxAmount());
        e.setDiscountAmount(invoice.discountAmount());
        e.setTotal(invoice.total());
        e.setPaymentForm(invoice.paymentForm());
        e.setPaymentMeans(invoice.paymentMeans());
        e.setCufe(invoice.cufe());
        e.setQrData(invoice.qrData());
        e.setProvider(invoice.provider());
        e.setProviderReference(invoice.providerReference());
        e.setStatusDetail(truncate(invoice.statusDetail(), 500));
        e.setReason(invoice.reason());
        e.setDevolutionId(invoice.devolutionId());
        e.setCreditScope(invoice.partial() ? "PARTIAL" : "TOTAL");
        InvoiceEntity saved = jpa.saveAndFlush(e);
        if (isNew) {
            // Las líneas son inmutables: se escriben solo al crear el documento.
            for (InvoiceLine line : invoice.lines()) {
                InvoiceLineEntity l = new InvoiceLineEntity();
                l.setInvoiceId(saved.getId());
                l.setOrderLineId(line.orderLineId());
                l.setPosition(line.position());
                l.setSkuCode(line.skuCode());
                l.setDescription(truncate(line.description(), 255));
                l.setQuantity(line.quantity());
                l.setUnitPrice(line.unitPrice());
                l.setDiscount(line.discount());
                l.setTaxCode(line.taxCode());
                l.setTaxKind(line.taxKind());
                l.setTaxRate(line.taxRate());
                l.setTaxBase(line.taxBase());
                l.setTaxAmount(line.taxAmount());
                l.setTotal(line.total());
                lineJpa.save(l);
            }
            lineJpa.flush();
        }
        return toDomain(saved, lineJpa.findByInvoiceIdOrderByPositionAsc(saved.getId()));
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return jpa.findById(id).map(this::withLines);
    }

    @Override
    public Optional<Invoice> lockById(Long id) {
        return jpa.lockById(id).map(this::withLines);
    }

    @Override
    public List<Invoice> findByOrderId(Long orderId) {
        return withLines(jpa.findByOrderIdOrderByIdAsc(orderId));
    }

    @Override
    public Optional<Invoice> activeInvoiceFor(Long orderId) {
        return jpa.activeInvoicesFor(orderId).stream().findFirst().map(this::withLines);
    }

    @Override
    public Optional<Invoice> creditNoteFor(Long invoiceId) {
        return jpa.findByKindAndRelatedInvoiceIdOrderByIdDesc(DocumentKind.CREDIT_NOTE.name(), invoiceId).stream()
                .filter(note -> !"PARTIAL".equals(note.getCreditScope()))
                .findFirst().map(this::withLines);
    }

    @Override
    public List<Invoice> creditNotesFor(Long invoiceId) {
        return withLines(jpa.findByKindAndRelatedInvoiceIdOrderByIdDesc(DocumentKind.CREDIT_NOTE.name(), invoiceId));
    }

    @Override
    public Optional<Invoice> findByDevolutionId(Long devolutionId) {
        return jpa.findByDevolutionId(devolutionId).map(this::withLines);
    }

    @Override
    public Page<Invoice> search(InvoiceFilter filter, Pageable pageable) {
        Specification<InvoiceEntity> spec = (root, query, cb) -> cb.and(predicates(filter, root, cb).toArray(Predicate[]::new));
        Page<InvoiceEntity> page = jpa.findAll(spec, pageable);
        return new PageImpl<>(withLines(page.getContent()), pageable, page.getTotalElements());
    }

    @Override
    public Totals totals(InvoiceFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<InvoiceEntity> root = q.from(InvoiceEntity.class);
        Expression<String> kind = root.get("kind");
        Expression<String> status = root.get("status");
        Predicate invoice = cb.equal(kind, DocumentKind.INVOICE.name());
        Predicate note = cb.equal(kind, DocumentKind.CREDIT_NOTE.name());
        // Cuenta lo emitido (también facturas anuladas: su nota crédito resta).
        Predicate counted = status.in(InvoiceStatus.ISSUED.name(), InvoiceStatus.VOIDED.name());
        Predicate pending = status.in(InvoiceStatus.PENDING.name(), InvoiceStatus.REJECTED.name());
        Expression<BigDecimal> total = root.get("total");
        Expression<BigDecimal> tax = root.get("taxAmount");
        q.multiselect(
                cb.sum(cb.<Long>selectCase().when(invoice, 1L).otherwise(0L)),
                cb.sum(cb.<Long>selectCase().when(note, 1L).otherwise(0L)),
                cb.sum(cb.<Long>selectCase().when(pending, 1L).otherwise(0L)),
                cb.sum(cb.<BigDecimal>selectCase().when(cb.and(invoice, counted), total).otherwise(BigDecimal.ZERO)),
                cb.sum(cb.<BigDecimal>selectCase().when(cb.and(note, counted), total).otherwise(BigDecimal.ZERO)),
                cb.sum(cb.<BigDecimal>selectCase().when(cb.and(invoice, counted), tax).otherwise(BigDecimal.ZERO)),
                cb.sum(cb.<BigDecimal>selectCase().when(cb.and(note, counted), tax).otherwise(BigDecimal.ZERO)))
                .where(predicates(filter, root, cb).toArray(Predicate[]::new));
        Tuple t = em.createQuery(q).getSingleResult();
        return new Totals(count(t.get(0)), count(t.get(1)), count(t.get(2)), money(t.get(3)), money(t.get(4)),
                money(t.get(5)), money(t.get(6)));
    }

    private static List<Predicate> predicates(InvoiceFilter f, Root<InvoiceEntity> root, CriteriaBuilder cb) {
        List<Predicate> list = new ArrayList<>();
        if (f == null) {
            return list;
        }
        if (f.kind() != null && !f.kind().isBlank()) {
            list.add(cb.equal(root.get("kind"), f.kind().trim().toUpperCase(Locale.ROOT)));
        }
        if (f.status() != null && !f.status().isBlank()) {
            list.add(root.get("status").in(List.of(f.status().toUpperCase(Locale.ROOT).split(",")).stream().map(String::trim).toList()));
        }
        if (f.orderId() != null) {
            list.add(cb.equal(root.get("orderId"), f.orderId()));
        }
        if (f.from() != null) {
            list.add(cb.greaterThanOrEqualTo(root.<Instant>get("issuedAt"), f.from()));
        }
        if (f.to() != null) {
            list.add(cb.lessThan(root.<Instant>get("issuedAt"), f.to()));
        }
        if (f.q() != null && !f.q().isBlank()) {
            String q = f.q().trim();
            String like = "%" + q.toLowerCase(Locale.ROOT).replace("%", "\\%").replace("_", "\\_") + "%";
            List<Predicate> any = new ArrayList<>();
            any.add(cb.like(cb.lower(root.get("fullNumber")), like));
            any.add(cb.like(cb.lower(root.get("buyerName")), like));
            any.add(cb.like(cb.lower(root.get("buyerDocNumber")), like));
            any.add(cb.like(cb.lower(root.get("cufe")), q.toLowerCase(Locale.ROOT) + "%"));
            String digits = q.startsWith("#") ? q.substring(1) : q;
            if (digits.matches("\\d{1,18}")) {
                any.add(cb.equal(root.get("orderId"), Long.parseLong(digits)));
            }
            list.add(cb.or(any.toArray(Predicate[]::new)));
        }
        return list;
    }

    private Invoice withLines(InvoiceEntity entity) {
        return toDomain(entity, lineJpa.findByInvoiceIdOrderByPositionAsc(entity.getId()));
    }

    private List<Invoice> withLines(List<InvoiceEntity> entities) {
        if (entities.isEmpty()) {
            return List.of();
        }
        Map<Long, List<InvoiceLineEntity>> lines = lineJpa
                .findByInvoiceIdInOrderByInvoiceIdAscPositionAsc(entities.stream().map(InvoiceEntity::getId).toList())
                .stream().collect(Collectors.groupingBy(InvoiceLineEntity::getInvoiceId));
        return entities.stream().map(e -> toDomain(e, lines.getOrDefault(e.getId(), List.of()))).toList();
    }

    static Invoice toDomain(InvoiceEntity e, List<InvoiceLineEntity> lines) {
        List<InvoiceLine> domainLines = lines.stream()
                .map(l -> new InvoiceLine(l.getId(), l.getPosition(), l.getOrderLineId(), l.getSkuCode(), l.getDescription(),
                        l.getQuantity(), l.getUnitPrice(), l.getDiscount(), l.getTaxCode(), l.getTaxKind(), l.getTaxRate(),
                        l.getTaxBase(), l.getTaxAmount(), l.getTotal()))
                .toList();
        Buyer buyer = new Buyer(e.getBuyerDocType(), e.getBuyerDocNumber(), e.getBuyerDv(), e.getBuyerName(), e.getBuyerEmail(),
                e.getBuyerPhone(), e.getBuyerAddress(), e.getBuyerCity());
        return new Invoice(e.getId(), DocumentKind.valueOf(e.getKind()), e.getOrderId(), e.getRelatedInvoiceId(),
                e.getResolutionId(), e.getPrefix(), e.getNumber(), e.getIssuedAt(), InvoiceStatus.valueOf(e.getStatus()),
                BillingEnvironment.parse(e.getEnvironment()), buyer, e.getPaymentForm(), e.getPaymentMeans(), e.getCufe(),
                e.getQrData(), e.getProvider(), e.getProviderReference(), e.getStatusDetail(), e.getReason(), domainLines,
                "PARTIAL".equals(e.getCreditScope()), e.getDevolutionId());
    }

    private static long count(Object value) {
        return value == null ? 0 : ((Number) value).longValue();
    }

    private static BigDecimal money(Object value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2);
        }
        return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
    }

    private static String truncate(String value, int max) {
        return value == null || value.length() <= max ? value : value.substring(0, max);
    }
}
