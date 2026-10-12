package com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;

/** Pago de una orden (V8). */
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class PaymentEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_payment")
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_type_id", nullable = false)
    private Long paymentTypeId;

    @Column(name = "channel", nullable = false, length = 20)
    private String channel;

    @Column(name = "kind", nullable = false, length = 30)
    private String kind;

    @Column(name = "provider", length = 30)
    private String provider;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "fee_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal feeAmount;

    @Column(name = "tendered_amount", precision = 15, scale = 2)
    private BigDecimal tenderedAmount;

    @Column(name = "change_amount", precision = 15, scale = 2)
    private BigDecimal changeAmount;

    @Column(name = "refunded_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal refundedAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "reference", length = 120)
    private String reference;

    @Column(name = "payment_reference", nullable = false, length = 60, unique = true)
    private String paymentReference;

    @Column(name = "provider_reference", length = 120)
    private String providerReference;

    @Column(name = "checkout_url", length = 1000)
    private String checkoutUrl;

    @Column(name = "return_url", length = 1000)
    private String returnUrl;

    @Column(name = "payer_email", length = 160)
    private String payerEmail;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "status_detail")
    private String statusDetail;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
