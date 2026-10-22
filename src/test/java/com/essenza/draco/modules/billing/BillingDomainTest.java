package com.essenza.draco.modules.billing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.billing.domain.model.BillingEnvironment;
import com.essenza.draco.modules.billing.domain.model.BillingRuleViolation;
import com.essenza.draco.modules.billing.domain.model.Buyer;
import com.essenza.draco.modules.billing.domain.model.Cufe;
import com.essenza.draco.modules.billing.domain.model.DocumentKind;
import com.essenza.draco.modules.billing.domain.model.Invoice;
import com.essenza.draco.modules.billing.domain.model.InvoiceLine;
import com.essenza.draco.modules.billing.domain.model.InvoiceStatus;
import com.essenza.draco.modules.billing.domain.model.Nit;
import com.essenza.draco.modules.billing.domain.model.NumberingResolution;
import com.essenza.draco.modules.billing.domain.model.TaxKind;
import com.essenza.draco.modules.billing.domain.model.TaxRate;

class BillingDomainTest {

    @Test
    void cufeMatchesDianAnnexExample() {
        // Ejemplo del anexo técnico de factura electrónica de la DIAN.
        String cufe = Cufe.compute("323200000129", "2019-01-16", "10:53:10-05:00", new BigDecimal("1500000.00"),
                new BigDecimal("285000.00"), BigDecimal.ZERO, new BigDecimal("1785000.00"), "700085371", "800199436",
                "693ff6f2a553c3646a063436fd4dd9ded0311471", BillingEnvironment.PRODUCTION);
        assertThat(cufe).isEqualTo(
                "8bb918b19ba22a694f1da11c643b5e9de39adf60311cf179179e9b33381030bcd4c3c3f156c506ed5908f9276f5bd9b4");
    }

    @Test
    void nitCheckDigit() {
        assertThat(Nit.checkDigit("900373115")).isEqualTo("3");
        assertThat(Nit.checkDigit("800.197.268")).isEqualTo("4");
        assertThat(Nit.checkDigit("860002964")).isEqualTo("4");
    }

    @Test
    void buyerDefaultsAndValidation() {
        Buyer consumer = Buyer.finalConsumer();
        assertThat(consumer.isFinalConsumer()).isTrue();
        Buyer company = new Buyer("NIT", "900.373.115", null, "ACME SAS", "compras@acme.co", null, null, null);
        assertThat(company.docNumber()).isEqualTo("900373115");
        assertThat(company.dv()).isEqualTo("3");
        assertThatThrownBy(() -> new Buyer("XX", "1", null, "A", null, null, null, null)).isInstanceOf(BillingRuleViolation.class);
        assertThatThrownBy(() -> new Buyer("CC", "123", null, "A", "no-email", null, null, null))
                .isInstanceOf(BillingRuleViolation.class);
    }

    @Test
    void numberingTakesConsecutivesAndStops() {
        NumberingResolution r = new NumberingResolution(1L, DocumentKind.INVOICE, "sett", "187", LocalDate.of(2024, 1, 1), "k",
                10, 11, 10, LocalDate.of(2024, 1, 1), LocalDate.of(2030, 1, 1), BillingEnvironment.TEST, true);
        assertThat(r.prefix()).isEqualTo("SETT");
        LocalDate today = LocalDate.of(2026, 10, 12);
        assertThat(r.take(today)).isEqualTo(10);
        assertThat(r.take(today)).isEqualTo(11);
        assertThat(r.remaining()).isZero();
        assertThatThrownBy(() -> r.take(today)).isInstanceOf(BillingRuleViolation.class).hasMessageContaining("agotada");
        NumberingResolution expired = new NumberingResolution(2L, DocumentKind.INVOICE, "", null, null, null, 1, 5, 1, null,
                LocalDate.of(2025, 1, 1), BillingEnvironment.TEST, true);
        assertThatThrownBy(() -> expired.take(today)).hasMessageContaining("venció");
        assertThatThrownBy(() -> new NumberingResolution(3L, DocumentKind.INVOICE, "FE", null, null, null, 1, 5, 1, null, null,
                BillingEnvironment.PRODUCTION, true)).hasMessageContaining("producción");
    }

    @Test
    void taxRateRules() {
        assertThatThrownBy(() -> new TaxRate(null, "EXC", "Excluido", null, TaxKind.EXCLUDED, BigDecimal.ONE, false, true, 0))
                .isInstanceOf(BillingRuleViolation.class);
        assertThatThrownBy(() -> new TaxRate(null, "IVA_19", "IVA", null, TaxKind.IVA, BigDecimal.valueOf(19), true, false, 0))
                .hasMessageContaining("por defecto");
        assertThatThrownBy(() -> new TaxRate(null, "iva", "IVA", null, TaxKind.IVA, BigDecimal.valueOf(19), false, true, 0))
                .hasMessageContaining("código");
    }

    @Test
    void invoiceTotalsAndTransitions() {
        InvoiceLine a = new InvoiceLine(null, 1, 1L, "A", "Perfume", 2, new BigDecimal("59500.00"), BigDecimal.ZERO, "IVA_19",
                "IVA", new BigDecimal("19.000"), new BigDecimal("100000.00"), new BigDecimal("19000.00"), new BigDecimal("119000.00"));
        InvoiceLine b = new InvoiceLine(null, 2, 2L, "B", "Libro", 1, new BigDecimal("20000.00"), BigDecimal.ZERO, "EXCLUIDO",
                "EXCLUDED", BigDecimal.ZERO, new BigDecimal("20000.00"), new BigDecimal("0.00"), new BigDecimal("20000.00"));
        Invoice invoice = new Invoice(null, DocumentKind.INVOICE, 7L, null, 1L, "SETT", 1, Instant.now(), InvoiceStatus.PENDING,
                BillingEnvironment.TEST, Buyer.finalConsumer(), "CASH", "10 Efectivo", null, null, "SIMULATOR", null, null, null,
                List.of(a, b));
        assertThat(invoice.netAmount()).isEqualByComparingTo("120000");
        assertThat(invoice.taxAmount()).isEqualByComparingTo("19000");
        assertThat(invoice.total()).isEqualByComparingTo("139000");
        assertThat(invoice.taxOf(TaxKind.IVA)).isEqualByComparingTo("19000");
        assertThat(invoice.fullNumber()).isEqualTo("SETT1");
        assertThatThrownBy(() -> invoice.markVoided("x")).isInstanceOf(BillingRuleViolation.class);
        invoice.markRejected("falló");
        assertThat(invoice.canRetry()).isTrue();
        invoice.markIssued("cufe", "qr", "ref", "ok");
        assertThat(invoice.canCredit()).isTrue();
        invoice.markVoided("NC");
        assertThat(invoice.status()).isEqualTo(InvoiceStatus.VOIDED);
        assertThatThrownBy(() -> new InvoiceLine(null, 1, 1L, "A", "X", 1, BigDecimal.TEN, BigDecimal.ZERO, null, null,
                BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN)).hasMessageContaining("no cuadra");
    }
}
