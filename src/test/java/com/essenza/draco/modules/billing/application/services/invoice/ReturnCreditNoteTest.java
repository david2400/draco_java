package com.essenza.draco.modules.billing.application.services.invoice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.billing.domain.model.BillingEnvironment;
import com.essenza.draco.modules.billing.domain.model.BillingRuleViolation;
import com.essenza.draco.modules.billing.domain.model.Buyer;
import com.essenza.draco.modules.billing.domain.model.DocumentKind;
import com.essenza.draco.modules.billing.domain.model.Invoice;
import com.essenza.draco.modules.billing.domain.model.InvoiceLine;
import com.essenza.draco.modules.billing.domain.model.InvoiceStatus;
import com.essenza.draco.shared.common.devolution.DevolutionRefundedEvent;

class ReturnCreditNoteTest {

    private static Invoice invoice() {
        return new Invoice(1L, DocumentKind.INVOICE, 99L, null, 1L, "SETT", 10, Instant.now(), InvoiceStatus.ISSUED,
                BillingEnvironment.TEST, Buyer.finalConsumer(), "CASH", "10 Efectivo", "cufe", "qr", "SIMULATOR", null,
                null, null, List.of(
                new InvoiceLine(11L, 1, 10L, "SKU-A", "Camisa", 3, new BigDecimal("33333.33"), BigDecimal.ZERO, "IVA_19",
                        "IVA", new BigDecimal("19"), new BigDecimal("84033.61"), new BigDecimal("15966.39"),
                        new BigDecimal("100000.00")),
                new InvoiceLine(12L, 2, 20L, "SKU-B", "Libro", 1, new BigDecimal("50000.00"), BigDecimal.ZERO, "EXCLUIDO",
                        "EXCLUDED", BigDecimal.ZERO, new BigDecimal("50000.00"), BigDecimal.ZERO.setScale(2),
                        new BigDecimal("50000.00"))));
    }

    @Test
    void partialLinesKeepRateAndSplitTax() {
        List<InvoiceLine> lines = CreateReturnCreditNoteService.creditLines(invoice(), new DevolutionRefundedEvent(5L, 99L,
                "Devolución #5", List.of(new DevolutionRefundedEvent.Line(10L, 2, new BigDecimal("61666.67")),
                new DevolutionRefundedEvent.Line(20L, 1, new BigDecimal("50000")))));
        assertThat(lines).hasSize(2);
        InvoiceLine shirt = lines.get(0);
        assertThat(shirt.quantity()).isEqualTo(2);
        assertThat(shirt.taxBase()).isEqualByComparingTo("51820.73");
        assertThat(shirt.taxAmount()).isEqualByComparingTo("9845.94");
        assertThat(shirt.total()).isEqualByComparingTo("61666.67");
        assertThat(lines.get(1).taxAmount()).isEqualByComparingTo("0");
        Invoice note = new Invoice(null, DocumentKind.CREDIT_NOTE, 99L, 1L, 2L, "NC", 1, Instant.now(), InvoiceStatus.PENDING,
                BillingEnvironment.TEST, Buyer.finalConsumer(), "CASH", null, null, null, "SIMULATOR", null, null, "x",
                lines, true, 5L);
        assertThat(note.partial()).isTrue();
        assertThat(note.total()).isEqualByComparingTo("111666.67");
    }

    @Test
    void cannotCreditMoreThanBilled() {
        assertThatThrownBy(() -> CreateReturnCreditNoteService.creditLines(invoice(), new DevolutionRefundedEvent(5L, 99L,
                "x", List.of(new DevolutionRefundedEvent.Line(10L, 4, new BigDecimal("1000"))))))
                .isInstanceOf(BillingRuleViolation.class);
        assertThatThrownBy(() -> CreateReturnCreditNoteService.creditLines(invoice(), new DevolutionRefundedEvent(5L, 99L,
                "x", List.of(new DevolutionRefundedEvent.Line(30L, 1, new BigDecimal("1000"))))))
                .hasMessageContaining("no está en la factura");
        assertThatThrownBy(() -> new Invoice(null, DocumentKind.INVOICE, 99L, null, 2L, "NC", 1, Instant.now(),
                InvoiceStatus.PENDING, BillingEnvironment.TEST, Buyer.finalConsumer(), "CASH", null, null, null, "SIMULATOR",
                null, null, "x", invoice().lines(), true, 5L)).isInstanceOf(BillingRuleViolation.class);
    }
}
