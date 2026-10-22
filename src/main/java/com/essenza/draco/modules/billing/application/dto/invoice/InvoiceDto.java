package com.essenza.draco.modules.billing.application.dto.invoice;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private Long id;
    @Schema(description = "INVOICE o CREDIT_NOTE")
    private String kind;
    private Long orderId;
    @Schema(description = "Factura anulada por esta nota crédito / nota que anuló esta factura")
    private Long relatedInvoiceId;
    private String relatedNumber;
    private String prefix;
    private Long number;
    private String fullNumber;
    private Instant issuedAt;
    @Schema(description = "PENDING, ISSUED, REJECTED o VOIDED")
    private String status;
    private String environment;
    private BuyerDto buyer;
    private BigDecimal netAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal total;
    private String currency;
    @Schema(description = "CASH (contado) o CREDIT")
    private String paymentForm;
    private String paymentMeans;
    @Schema(description = "CUFE (factura) o CUDE (nota crédito)")
    private String cufe;
    @Schema(description = "Contenido del código QR")
    private String qrData;
    @Schema(description = "URL de consulta en la DIAN")
    private String verificationUrl;
    private String provider;
    private String providerReference;
    private String statusDetail;
    private String reason;
    private String resolutionNumber;
    private String resolutionRange;
    private java.time.LocalDate resolutionValidTo;
    private List<InvoiceLineDto> lines;
    private List<InvoiceTaxDto> taxes;
    @Schema(description = "Acciones posibles: retry, credit_note")
    private List<String> actions;
    /** V10: nota crédito TOTAL o PARTIAL y devolución que la originó. */
    private String creditScope;
    private Long devolutionId;
    /** Factura: suma de las notas crédito vigentes (no rechazadas). */
    private BigDecimal creditedAmount;
}
