package com.essenza.draco.modules.billing.application.output.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceFilter;
import com.essenza.draco.modules.billing.domain.model.Invoice;

public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    Optional<Invoice> findById(Long id);

    Optional<Invoice> lockById(Long id);

    List<Invoice> findByOrderId(Long orderId);

    /** Factura vigente (no anulada) de la orden. */
    Optional<Invoice> activeInvoiceFor(Long orderId);

    /** Nota crédito total que anuló la factura, si la hay. */
    Optional<Invoice> creditNoteFor(Long invoiceId);

    /** Todas las notas crédito de la factura (totales y parciales), la más reciente primero. */
    List<Invoice> creditNotesFor(Long invoiceId);

    /** Nota crédito emitida por una devolución. */
    Optional<Invoice> findByDevolutionId(Long devolutionId);

    Page<Invoice> search(InvoiceFilter filter, Pageable pageable);

    Totals totals(InvoiceFilter filter);

    record Totals(long invoices, long creditNotes, long pending, BigDecimal invoicedTotal, BigDecimal creditedTotal,
                  BigDecimal invoicedTax, BigDecimal creditedTax) {
    }
}
