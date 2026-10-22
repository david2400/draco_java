package com.essenza.draco.modules.billing.application.services.invoice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;
import com.essenza.draco.modules.billing.application.dto.invoice.IssueInvoiceDto;
import com.essenza.draco.modules.billing.application.input.invoice.IssueInvoiceUseCase;
import com.essenza.draco.modules.billing.application.output.repository.InvoiceRepository;
import com.essenza.draco.modules.billing.application.output.repository.ResolutionRepository;
import com.essenza.draco.modules.billing.domain.model.BillingSettings;
import com.essenza.draco.modules.billing.domain.model.DocumentKind;
import com.essenza.draco.modules.billing.domain.model.Invoice;
import com.essenza.draco.modules.billing.domain.model.InvoiceStatus;
import com.essenza.draco.modules.billing.domain.model.NumberingResolution;
import com.essenza.draco.shared.common.sales.OrderBilling;
import com.essenza.draco.shared.common.sales.OrderBilling.BillableOrder;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

/**
 * Factura una orden pagada: toma el consecutivo de la numeración activa (bloqueada),
 * copia líneas e impuestos congelados de la orden y la envía al proveedor.
 */
@Service
@Transactional
public class IssueInvoiceService implements IssueInvoiceUseCase {

    private final InvoiceSoporte soporte;
    private final OrderBilling orders;
    private final InvoiceRepository invoices;
    private final ResolutionRepository resolutions;

    public IssueInvoiceService(InvoiceSoporte soporte, OrderBilling orders, InvoiceRepository invoices,
                               ResolutionRepository resolutions) {
        this.soporte = soporte;
        this.orders = orders;
        this.invoices = invoices;
        this.resolutions = resolutions;
    }

    @Override
    public InvoiceDto issue(IssueInvoiceDto input) {
        if (input.getOrderId() == null) {
            throw new IllegalArgumentException("Indica la orden a facturar (order_id).");
        }
        BillingSettings issuer = soporte.readyIssuer();
        BillableOrder order = orders.find(input.getOrderId())
                .orElseThrow(() -> new NotFoundException("No se encontró la orden " + input.getOrderId() + "."));
        InvoiceSoporte.checkBillable(order);
        invoices.activeInvoiceFor(order.id()).ifPresent(existing -> {
            throw new ConflictException("La orden " + order.id() + " ya tiene la factura " + existing.fullNumber()
                    + ": anúlala con una nota crédito para volver a facturar.");
        });
        NumberingResolution resolution = resolutions.lockActive(DocumentKind.INVOICE, issuer.environment())
                .orElseThrow(() -> new ConflictException("No hay una numeración de facturas activa para el ambiente "
                        + issuer.environment() + ": regístrala en Facturación → Numeración."));
        long number = resolution.take(InvoiceSoporte.today());
        resolutions.save(resolution);

        Invoice invoice = invoices.save(new Invoice(null, DocumentKind.INVOICE, order.id(), null, resolution.id(),
                resolution.prefix(), number, InvoiceSoporte.now(), InvoiceStatus.PENDING, issuer.environment(),
                InvoiceSoporte.buyerOf(input.getBuyer() != null ? input.getBuyer() : InvoiceSoporte.buyerFrom(order.buyer())), "CASH", InvoiceSoporte.paymentMeans(order.paidKinds()), null, null,
                issuer.provider(), null, null, null, InvoiceSoporte.linesOf(order)));
        return soporte.toDto(soporte.send(invoice, issuer));
    }
}
