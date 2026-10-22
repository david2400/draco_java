package com.essenza.draco.modules.billing.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Factura o nota crédito. Los totales salen de las líneas; las transiciones de estado
 * se validan aquí: PENDING/REJECTED → ISSUED | REJECTED, ISSUED → VOIDED (solo facturas).
 */
public final class Invoice {

    private final Long id;
    private final DocumentKind kind;
    private final Long orderId;
    private final Long relatedInvoiceId;
    private final Long resolutionId;
    private final String prefix;
    private final long number;
    private final Instant issuedAt;
    private InvoiceStatus status;
    private final BillingEnvironment environment;
    private final Buyer buyer;
    private final BigDecimal netAmount;
    private final BigDecimal taxAmount;
    private final BigDecimal discountAmount;
    private final BigDecimal total;
    private final String paymentForm;
    private final String paymentMeans;
    private String cufe;
    private String qrData;
    private final String provider;
    private String providerReference;
    private String statusDetail;
    private final String reason;
    private final List<InvoiceLine> lines;
    /** Nota crédito parcial (por una devolución); las totales anulan la factura al emitirse. */
    private final boolean partial;
    private final Long devolutionId;

    public Invoice(Long id, DocumentKind kind, Long orderId, Long relatedInvoiceId, Long resolutionId, String prefix, long number,
                   Instant issuedAt, InvoiceStatus status, BillingEnvironment environment, Buyer buyer, String paymentForm,
                   String paymentMeans, String cufe, String qrData, String provider, String providerReference,
                   String statusDetail, String reason, List<InvoiceLine> lines) {
        this(id, kind, orderId, relatedInvoiceId, resolutionId, prefix, number, issuedAt, status, environment, buyer,
                paymentForm, paymentMeans, cufe, qrData, provider, providerReference, statusDetail, reason, lines, false, null);
    }

    public Invoice(Long id, DocumentKind kind, Long orderId, Long relatedInvoiceId, Long resolutionId, String prefix, long number,
                   Instant issuedAt, InvoiceStatus status, BillingEnvironment environment, Buyer buyer, String paymentForm,
                   String paymentMeans, String cufe, String qrData, String provider, String providerReference,
                   String statusDetail, String reason, List<InvoiceLine> lines, boolean partial, Long devolutionId) {
        if (partial && kind != DocumentKind.CREDIT_NOTE) {
            throw new BillingRuleViolation("Solo una nota crédito puede ser parcial.");
        }
        this.partial = partial;
        this.devolutionId = devolutionId;
        if (lines == null || lines.isEmpty()) {
            throw new BillingRuleViolation("El documento no tiene líneas.");
        }
        if (kind == DocumentKind.CREDIT_NOTE && relatedInvoiceId == null) {
            throw new BillingRuleViolation("La nota crédito debe referenciar la factura que anula.");
        }
        this.id = id;
        this.kind = kind;
        this.orderId = orderId;
        this.relatedInvoiceId = relatedInvoiceId;
        this.resolutionId = resolutionId;
        this.prefix = prefix == null ? "" : prefix;
        this.number = number;
        this.issuedAt = issuedAt;
        this.status = status;
        this.environment = environment;
        this.buyer = buyer;
        this.paymentForm = paymentForm == null ? "CASH" : paymentForm;
        this.paymentMeans = paymentMeans;
        this.cufe = cufe;
        this.qrData = qrData;
        this.provider = provider;
        this.providerReference = providerReference;
        this.statusDetail = statusDetail;
        this.reason = reason;
        this.lines = List.copyOf(lines);
        this.netAmount = this.lines.stream().map(InvoiceLine::taxBase).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.taxAmount = this.lines.stream().map(InvoiceLine::taxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.discountAmount = this.lines.stream().map(InvoiceLine::discount).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.total = this.lines.stream().map(InvoiceLine::total).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.signum() <= 0) {
            throw new BillingRuleViolation("El total del documento debe ser mayor que cero.");
        }
    }

    public String fullNumber() {
        return prefix + number;
    }

    /** IVA del documento (para el CUFE). */
    public BigDecimal taxOf(TaxKind kind) {
        return lines.stream().filter(line -> kind.name().equals(line.taxKind()))
                .map(InvoiceLine::taxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void markIssued(String cufe, String qrData, String providerReference, String detail) {
        if (status != InvoiceStatus.PENDING && status != InvoiceStatus.REJECTED) {
            throw new BillingRuleViolation("El documento " + fullNumber() + " ya está " + status + ".");
        }
        this.status = InvoiceStatus.ISSUED;
        this.cufe = cufe;
        this.qrData = qrData;
        this.providerReference = providerReference;
        this.statusDetail = detail;
    }

    public void markRejected(String detail) {
        if (status != InvoiceStatus.PENDING && status != InvoiceStatus.REJECTED) {
            throw new BillingRuleViolation("El documento " + fullNumber() + " ya está " + status + ".");
        }
        this.status = InvoiceStatus.REJECTED;
        this.statusDetail = detail;
    }

    public void markVoided(String detail) {
        if (kind != DocumentKind.INVOICE || status != InvoiceStatus.ISSUED) {
            throw new BillingRuleViolation("Solo se anula con nota crédito una factura emitida (la " + fullNumber()
                    + " está " + status + ").");
        }
        this.status = InvoiceStatus.VOIDED;
        this.statusDetail = detail;
    }

    /** Deja una nota en un documento pendiente (p. ej. fallo de comunicación con el proveedor). */
    public void annotate(String detail) {
        this.statusDetail = detail;
    }

    public boolean canRetry() {
        return status == InvoiceStatus.PENDING || status == InvoiceStatus.REJECTED;
    }

    public boolean canCredit() {
        return kind == DocumentKind.INVOICE && status == InvoiceStatus.ISSUED;
    }

    public Long id() { return id; }
    public DocumentKind kind() { return kind; }
    public Long orderId() { return orderId; }
    public Long relatedInvoiceId() { return relatedInvoiceId; }
    public Long resolutionId() { return resolutionId; }
    public String prefix() { return prefix; }
    public long number() { return number; }
    public Instant issuedAt() { return issuedAt; }
    public InvoiceStatus status() { return status; }
    public BillingEnvironment environment() { return environment; }
    public Buyer buyer() { return buyer; }
    public BigDecimal netAmount() { return netAmount; }
    public BigDecimal taxAmount() { return taxAmount; }
    public BigDecimal discountAmount() { return discountAmount; }
    public BigDecimal total() { return total; }
    public String paymentForm() { return paymentForm; }
    public String paymentMeans() { return paymentMeans; }
    public String cufe() { return cufe; }
    public String qrData() { return qrData; }
    public String provider() { return provider; }
    public String providerReference() { return providerReference; }
    public String statusDetail() { return statusDetail; }
    public String reason() { return reason; }
    public List<InvoiceLine> lines() { return lines; }
    public boolean partial() { return partial; }
    public Long devolutionId() { return devolutionId; }
}
