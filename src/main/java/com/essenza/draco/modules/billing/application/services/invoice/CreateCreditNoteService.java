package com.essenza.draco.modules.billing.application.services.invoice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.invoice.CreditNoteDto;
import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;
import com.essenza.draco.modules.billing.application.input.invoice.CreateCreditNoteUseCase;
import com.essenza.draco.modules.billing.application.output.repository.InvoiceRepository;
import com.essenza.draco.modules.billing.application.output.repository.ResolutionRepository;
import com.essenza.draco.modules.billing.domain.model.BillingSettings;
import com.essenza.draco.modules.billing.domain.model.DocumentKind;
import com.essenza.draco.modules.billing.domain.model.Invoice;
import com.essenza.draco.modules.billing.domain.model.InvoiceStatus;
import com.essenza.draco.modules.billing.domain.model.NumberingResolution;
import com.essenza.draco.shared.exceptions.ConflictException;

/** Nota crédito total: copia las líneas de la factura y, al ser aceptada, la anula. */
@Service
@Transactional
public class CreateCreditNoteService implements CreateCreditNoteUseCase {

    private final InvoiceSoporte soporte;
    private final InvoiceRepository invoices;
    private final ResolutionRepository resolutions;

    public CreateCreditNoteService(InvoiceSoporte soporte, InvoiceRepository invoices, ResolutionRepository resolutions) {
        this.soporte = soporte;
        this.invoices = invoices;
        this.resolutions = resolutions;
    }

    @Override
    public InvoiceDto creditNote(Long invoiceId, CreditNoteDto input) {
        String reason = input == null || input.getReason() == null ? "" : input.getReason().trim();
        if (reason.isEmpty()) {
            throw new IllegalArgumentException("Indica el motivo de la nota crédito.");
        }
        BillingSettings issuer = soporte.readyIssuer();
        Invoice invoice = soporte.locked(invoiceId);
        if (!invoice.canCredit()) {
            throw new ConflictException("Solo se anula con nota crédito una factura emitida (la " + invoice.fullNumber()
                    + " está " + invoice.status() + ").");
        }
        invoices.creditNoteFor(invoiceId).ifPresent(note -> {
            throw new ConflictException("La factura " + invoice.fullNumber() + " ya tiene la nota crédito "
                    + note.fullNumber() + (note.canRetry() ? " pendiente: reenvíala." : "."));
        });
        if (soporte.activeCredits(invoiceId).signum() > 0) {
            throw new ConflictException("La factura " + invoice.fullNumber() + " ya tiene notas crédito parciales por "
                    + "devoluciones: acredita el resto con una devolución.");
        }
        NumberingResolution resolution = resolutions.lockActive(DocumentKind.CREDIT_NOTE, invoice.environment())
                .orElseThrow(() -> new ConflictException("No hay una numeración de notas crédito activa para el ambiente "
                        + invoice.environment() + "."));
        long number = resolution.take(InvoiceSoporte.today());
        resolutions.save(resolution);
        Invoice note = invoices.save(new Invoice(null, DocumentKind.CREDIT_NOTE, invoice.orderId(), invoice.id(),
                resolution.id(), resolution.prefix(), number, InvoiceSoporte.now(), InvoiceStatus.PENDING,
                invoice.environment(), invoice.buyer(), invoice.paymentForm(), invoice.paymentMeans(), null, null,
                issuer.provider(), null, null, reason.length() > 500 ? reason.substring(0, 500) : reason,
                InvoiceSoporte.copyLines(invoice)));
        return soporte.toDto(soporte.send(note, issuer));
    }
}
