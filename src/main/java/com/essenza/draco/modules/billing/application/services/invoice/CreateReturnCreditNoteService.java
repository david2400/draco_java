package com.essenza.draco.modules.billing.application.services.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;
import com.essenza.draco.modules.billing.application.input.invoice.CreateReturnCreditNoteUseCase;
import com.essenza.draco.modules.billing.application.output.repository.InvoiceRepository;
import com.essenza.draco.modules.billing.application.output.repository.ResolutionRepository;
import com.essenza.draco.modules.billing.domain.model.BillingRuleViolation;
import com.essenza.draco.modules.billing.domain.model.BillingSettings;
import com.essenza.draco.modules.billing.domain.model.DocumentKind;
import com.essenza.draco.modules.billing.domain.model.Invoice;
import com.essenza.draco.modules.billing.domain.model.InvoiceLine;
import com.essenza.draco.modules.billing.domain.model.InvoiceStatus;
import com.essenza.draco.modules.billing.domain.model.NumberingResolution;
import com.essenza.draco.shared.common.devolution.DevolutionRefundedEvent;
import com.essenza.draco.shared.exceptions.ConflictException;

/**
 * Nota crédito parcial por una devolución: solo las líneas y cantidades devueltas, por el valor
 * acreditado (impuesto incluido, con la tarifa de la línea facturada). Si las notas acumuladas
 * cubren toda la factura, esta queda anulada al emitirse la última.
 */
@Service
public class CreateReturnCreditNoteService implements CreateReturnCreditNoteUseCase {

    private final InvoiceSoporte soporte;
    private final InvoiceRepository invoices;
    private final ResolutionRepository resolutions;

    public CreateReturnCreditNoteService(InvoiceSoporte soporte, InvoiceRepository invoices, ResolutionRepository resolutions) {
        this.soporte = soporte;
        this.invoices = invoices;
        this.resolutions = resolutions;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<InvoiceDto> forDevolution(DevolutionRefundedEvent event) {
        Optional<Invoice> existing = invoices.findByDevolutionId(event.devolutionId());
        if (existing.isPresent()) {
            return existing.map(soporte::toDto);
        }
        Optional<Invoice> active = invoices.activeInvoiceFor(event.orderId());
        if (active.isEmpty() || active.get().status() != InvoiceStatus.ISSUED) {
            return Optional.empty();
        }
        BillingSettings issuer = soporte.readyIssuer();
        Invoice invoice = soporte.locked(active.get().id());
        if (invoices.creditNoteFor(invoice.id()).filter(note -> note.status() != InvoiceStatus.REJECTED).isPresent()) {
            return Optional.empty();
        }
        List<InvoiceLine> lines = creditLines(invoice, event);
        BigDecimal total = lines.stream().map(InvoiceLine::total).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal available = invoice.total().subtract(soporte.activeCredits(invoice.id()));
        if (total.compareTo(available) > 0) {
            throw new BillingRuleViolation("La nota crédito (" + total.toPlainString() + ") supera lo que queda por acreditar de la "
                    + "factura " + invoice.fullNumber() + " (" + available.toPlainString() + ").");
        }
        NumberingResolution resolution = resolutions.lockActive(DocumentKind.CREDIT_NOTE, invoice.environment())
                .orElseThrow(() -> new ConflictException("No hay una numeración de notas crédito activa para el ambiente "
                        + invoice.environment() + "."));
        long number = resolution.take(InvoiceSoporte.today());
        resolutions.save(resolution);
        String reason = event.reason() == null || event.reason().isBlank() ? "Devolución #" + event.devolutionId() : event.reason();
        Invoice note = invoices.save(new Invoice(null, DocumentKind.CREDIT_NOTE, invoice.orderId(), invoice.id(),
                resolution.id(), resolution.prefix(), number, InvoiceSoporte.now(), InvoiceStatus.PENDING,
                invoice.environment(), invoice.buyer(), invoice.paymentForm(), invoice.paymentMeans(), null, null,
                issuer.provider(), null, null, reason.length() > 500 ? reason.substring(0, 500) : reason, lines,
                true, event.devolutionId()));
        return Optional.of(soporte.toDto(soporte.send(note, issuer)));
    }

    /** Líneas de la nota: misma tarifa que la línea facturada; base = valor / (1 + tarifa). */
    static List<InvoiceLine> creditLines(Invoice invoice, DevolutionRefundedEvent event) {
        List<InvoiceLine> lines = new ArrayList<>();
        int position = 1;
        for (DevolutionRefundedEvent.Line returned : event.lines()) {
            InvoiceLine billed = invoice.lines().stream()
                    .filter(line -> Objects.equals(line.orderLineId(), returned.orderLineId())).findFirst()
                    .orElseThrow(() -> new BillingRuleViolation("La línea " + returned.orderLineId()
                            + " no está en la factura " + invoice.fullNumber() + "."));
            BigDecimal amount = returned.amount().setScale(2, RoundingMode.HALF_UP);
            if (returned.quantity() > billed.quantity() || amount.compareTo(billed.total()) > 0) {
                throw new BillingRuleViolation("\"" + billed.description() + "\": lo devuelto supera lo facturado.");
            }
            BigDecimal rate = billed.taxRate() == null ? BigDecimal.ZERO : billed.taxRate();
            BigDecimal base = rate.signum() > 0
                    ? amount.divide(BigDecimal.ONE.add(rate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)), 2, RoundingMode.HALF_UP)
                    : amount;
            BigDecimal tax = amount.subtract(base);
            BigDecimal unit = amount.divide(BigDecimal.valueOf(returned.quantity()), 2, RoundingMode.HALF_UP);
            lines.add(new InvoiceLine(null, position++, billed.orderLineId(), billed.skuCode(), billed.description(),
                    returned.quantity(), unit, BigDecimal.ZERO.setScale(2), billed.taxCode(), billed.taxKind(), rate,
                    base, tax, amount));
        }
        return lines;
    }
}
