package com.essenza.draco.modules.billing.infrastructure.inbound.rest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.essenza.draco.modules.billing.application.dto.invoice.CreditNoteDto;
import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;
import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceFilter;
import com.essenza.draco.modules.billing.application.dto.invoice.InvoicesSummaryDto;
import com.essenza.draco.modules.billing.application.dto.invoice.IssueInvoiceDto;
import com.essenza.draco.modules.billing.application.input.invoice.CreateCreditNoteUseCase;
import com.essenza.draco.modules.billing.application.input.invoice.FindInvoiceByIdUseCase;
import com.essenza.draco.modules.billing.application.input.invoice.FindOrderInvoicesUseCase;
import com.essenza.draco.modules.billing.application.input.invoice.IssueInvoiceUseCase;
import com.essenza.draco.modules.billing.application.input.invoice.RetryInvoiceUseCase;
import com.essenza.draco.modules.billing.application.input.invoice.SearchInvoicesUseCase;
import com.essenza.draco.modules.billing.application.input.invoice.SummarizeInvoicesUseCase;
import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.shared.common.web.PageableFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/billing")
@Tag(name = "Invoices", description = "Facturas electrónicas y notas crédito de las órdenes")
@RequiredArgsConstructor
public class InvoiceController {

    static final ZoneId ZONE = ZoneId.of("America/Bogota");
    private static final Set<String> SORTABLE = Set.of("id", "number", "issuedAt", "total", "status", "orderId");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("id"));

    private final SearchInvoicesUseCase searchInvoices;
    private final SummarizeInvoicesUseCase summarizeInvoices;
    private final FindInvoiceByIdUseCase findInvoice;
    private final FindOrderInvoicesUseCase findOrderInvoices;
    private final IssueInvoiceUseCase issueInvoice;
    private final RetryInvoiceUseCase retryInvoice;
    private final CreateCreditNoteUseCase createCreditNote;

    @Operation(summary = "Search", description = "Filtros: q (número, comprador, documento, CUFE, orden), kind (INVOICE | CREDIT_NOTE), "
            + "status (uno o varios separados por coma), orderId, from/to (yyyy-MM-dd, hora de Colombia).")
    @GetMapping("/invoices")
    public PageResponse<InvoiceDto> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "kind", required = false) String kind,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "orderId", required = false) Long orderId,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        return PageResponse.from(searchInvoices.search(filter(q, kind, status, orderId, from, to),
                PageableFactory.of(page, size, sort, SORTABLE, DEFAULT_SORT)));
    }

    @Operation(summary = "Summary", description = "Facturado, notas crédito, neto e impuestos con los mismos filtros.")
    @GetMapping("/invoices/summary")
    public InvoicesSummaryDto summary(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "kind", required = false) String kind,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "orderId", required = false) Long orderId,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to) {
        return summarizeInvoices.summary(filter(q, kind, status, orderId, from, to));
    }

    @Operation(summary = "Get by id", description = "Documento con líneas, impuestos por tarifa, CUFE y QR.")
    @GetMapping("/invoices/{id}")
    public InvoiceDto get(@PathVariable Long id) {
        return findInvoice.findById(id);
    }

    @Operation(summary = "Issue", description = "Factura una orden pagada (comprador vacío = consumidor final).")
    @PostMapping("/invoices")
    public ResponseEntity<InvoiceDto> issue(@Valid @RequestBody IssueInvoiceDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueInvoice.issue(input));
    }

    @Operation(summary = "Retry", description = "Reenvía un documento pendiente o rechazado (conserva el número).")
    @PostMapping("/invoices/{id}/retry")
    public InvoiceDto retry(@PathVariable Long id) {
        return retryInvoice.retry(id);
    }

    @Operation(summary = "Credit note", description = "Nota crédito total que anula la factura.")
    @PostMapping("/invoices/{id}/credit_note")
    public ResponseEntity<InvoiceDto> creditNote(@PathVariable Long id, @Valid @RequestBody CreditNoteDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createCreditNote.creditNote(id, input));
    }

    @Operation(summary = "Order invoices", description = "Facturas y notas crédito de una orden.")
    @GetMapping("/orders/{orderId}/invoices")
    public List<InvoiceDto> forOrder(@PathVariable Long orderId) {
        return findOrderInvoices.forOrder(orderId);
    }

    static InvoiceFilter filter(String q, String kind, String status, Long orderId, LocalDate from, LocalDate to) {
        return new InvoiceFilter(q, kind, status, orderId,
                from == null ? null : from.atStartOfDay(ZONE).toInstant(),
                to == null ? null : to.plusDays(1).atStartOfDay(ZONE).toInstant());
    }
}
