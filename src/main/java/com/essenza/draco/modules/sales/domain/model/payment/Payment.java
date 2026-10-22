package com.essenza.draco.modules.sales.domain.model.payment;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Pago de una orden. Las transiciones de estado se validan aquí; el saldo de la orden se
 * calcula con {@link #settledAmount()} (lo aprobado menos lo reembolsado).
 */
public final class Payment {

    private final Long id;
    private final Long orderId;
    private final Long methodId;
    private final PaymentChannel channel;
    private final PaymentMethodKind kind;
    private final GatewayProvider provider;
    private PaymentStatus status;
    private final BigDecimal amount;
    private final BigDecimal feeAmount;
    private final BigDecimal tenderedAmount;
    private final BigDecimal changeAmount;
    private BigDecimal refundedAmount;
    private String reference;
    private final String paymentReference;
    private String providerReference;
    private String checkoutUrl;
    private final String returnUrl;
    private final String payerEmail;
    private final String notes;
    private String statusDetail;
    private Instant approvedAt;
    private final Instant expiresAt;
    private final Instant createdAt;
    private Long cashSessionId;

    public Payment(Long id, Long orderId, Long methodId, PaymentChannel channel, PaymentMethodKind kind,
                   GatewayProvider provider, PaymentStatus status, BigDecimal amount, BigDecimal feeAmount,
                   BigDecimal tenderedAmount, BigDecimal changeAmount, BigDecimal refundedAmount, String reference,
                   String paymentReference, String providerReference, String checkoutUrl, String returnUrl, String payerEmail,
                   String notes, String statusDetail, Instant approvedAt, Instant expiresAt, Instant createdAt) {
        if (amount == null || amount.signum() <= 0) {
            throw new PaymentRuleViolation("El monto del pago debe ser mayor que 0.");
        }
        this.id = id;
        this.orderId = orderId;
        this.methodId = methodId;
        this.channel = channel;
        this.kind = kind;
        this.provider = provider;
        this.status = status;
        this.amount = amount;
        this.feeAmount = feeAmount == null ? BigDecimal.ZERO : feeAmount;
        this.tenderedAmount = tenderedAmount;
        this.changeAmount = changeAmount;
        this.refundedAmount = refundedAmount == null ? BigDecimal.ZERO : refundedAmount;
        this.reference = reference;
        this.paymentReference = paymentReference;
        this.providerReference = providerReference;
        this.checkoutUrl = checkoutUrl;
        this.returnUrl = returnUrl;
        this.payerEmail = payerEmail;
        this.notes = notes;
        this.statusDetail = statusDetail;
        this.approvedAt = approvedAt;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    /** Liga el pago al turno de caja en el que se cobró. */
    public void assignCashSession(Long sessionId) {
        this.cashSessionId = sessionId;
    }

    public Long getCashSessionId() {
        return cashSessionId;
    }

    /** Lo que este pago aporta al saldo de la orden. */
    public BigDecimal settledAmount() {
        return status == PaymentStatus.APPROVED ? amount.subtract(refundedAmount) : BigDecimal.ZERO;
    }

    /** Pendiente de confirmar (cuenta como "en proceso", no como pagado). */
    public BigDecimal pendingAmount() {
        return status == PaymentStatus.PENDING ? amount : BigDecimal.ZERO;
    }

    public BigDecimal refundable() {
        return status == PaymentStatus.APPROVED ? amount.subtract(refundedAmount) : BigDecimal.ZERO;
    }

    private void moveTo(PaymentStatus target) {
        if (status == target) {
            return;
        }
        if (!status.canTransitionTo(target)) {
            throw new PaymentRuleViolation("No se puede pasar un pago de " + status + " a " + target + ".");
        }
        status = target;
    }

    public void approve(Instant at, String providerRef, String detail) {
        moveTo(PaymentStatus.APPROVED);
        if (approvedAt == null) {
            approvedAt = at;
        }
        if (providerRef != null && !providerRef.isBlank()) {
            providerReference = providerRef;
        }
        statusDetail = detail;
    }

    public void decline(String providerRef, String detail) {
        moveTo(PaymentStatus.DECLINED);
        if (providerRef != null && !providerRef.isBlank()) {
            providerReference = providerRef;
        }
        statusDetail = detail;
    }

    public void expire(String detail) {
        moveTo(PaymentStatus.EXPIRED);
        statusDetail = detail;
    }

    /** Anula un pago pendiente o aprobado (error de registro, voucher anulado el mismo día…). */
    public void voidPayment(String reason) {
        if (status == PaymentStatus.APPROVED && refundedAmount.signum() > 0) {
            throw new PaymentRuleViolation("El pago ya tiene reembolsos: no se puede anular, reembolsa el resto.");
        }
        moveTo(PaymentStatus.VOIDED);
        statusDetail = reason;
    }

    /** Reembolso parcial o total; el total deja el pago en REFUNDED. */
    public void refund(BigDecimal value, String reason) {
        if (status != PaymentStatus.APPROVED) {
            throw new PaymentRuleViolation("Solo se reembolsan pagos aprobados (este está " + status + ").");
        }
        if (value == null || value.signum() <= 0) {
            throw new PaymentRuleViolation("El monto a reembolsar debe ser mayor que 0.");
        }
        if (value.compareTo(refundable()) > 0) {
            throw new PaymentRuleViolation("Se puede reembolsar como máximo " + refundable().toPlainString() + ".");
        }
        refundedAmount = refundedAmount.add(value);
        if (refundedAmount.compareTo(amount) == 0) {
            status = PaymentStatus.REFUNDED;
        }
        statusDetail = reason;
    }

    /** Nota sobre el estado (p. ej. monto distinto informado por la pasarela). */
    public void annotate(String detail) {
        statusDetail = detail;
    }

    /** Referencia del comprobante (voucher, transferencia) informada al confirmar. */
    public void recordReference(String value) {
        if (value != null && !value.isBlank()) {
            reference = value.trim();
        }
    }

    /** Asocia el id de la transacción en la pasarela (si aún no lo tenía). */
    public void linkProvider(String providerRef) {
        if (providerRef != null && !providerRef.isBlank()) {
            providerReference = providerRef;
        }
    }

    public void attachCheckout(String url, String providerRef) {
        checkoutUrl = url;
        if (providerRef != null && !providerRef.isBlank()) {
            providerReference = providerRef;
        }
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getMethodId() { return methodId; }
    public PaymentChannel getChannel() { return channel; }
    public PaymentMethodKind getKind() { return kind; }
    public GatewayProvider getProvider() { return provider; }
    public PaymentStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getFeeAmount() { return feeAmount; }
    public BigDecimal getTenderedAmount() { return tenderedAmount; }
    public BigDecimal getChangeAmount() { return changeAmount; }
    public BigDecimal getRefundedAmount() { return refundedAmount; }
    public String getReference() { return reference; }
    public String getPaymentReference() { return paymentReference; }
    public String getProviderReference() { return providerReference; }
    public String getCheckoutUrl() { return checkoutUrl; }
    public String getReturnUrl() { return returnUrl; }
    public String getPayerEmail() { return payerEmail; }
    public String getNotes() { return notes; }
    public String getStatusDetail() { return statusDetail; }
    public Instant getApprovedAt() { return approvedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
}
