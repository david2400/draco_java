package com.essenza.draco.modules.sales.infrastructure.inbound.rest;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.essenza.draco.modules.sales.application.dto.order.BulkOrderStatusDto;
import com.essenza.draco.modules.sales.application.dto.order.CreateFullOrderDto;
import com.essenza.draco.modules.sales.application.dto.order.CustomerLookupDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderActionDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderEventDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderNumberingDto;
import com.essenza.draco.modules.sales.application.dto.order.PickOrderDto;
import com.essenza.draco.modules.sales.application.dto.order.SaveOrderDetailsDto;
import com.essenza.draco.modules.sales.application.dto.order.ShipOrderDto;
import com.essenza.draco.modules.sales.application.input.order.BulkChangeOrderStatusUseCase;
import com.essenza.draco.modules.sales.application.input.order.CreateFullOrderUseCase;
import com.essenza.draco.modules.sales.application.input.order.OrderFulfillmentUseCase;
import com.essenza.draco.modules.sales.application.input.order.OrderInsightsUseCase;
import com.essenza.draco.modules.sales.application.input.order.OrderTimelineUseCase;
import com.essenza.draco.modules.sales.application.input.order.SaveOrderDetailsUseCase;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;

/**
 * Operación de órdenes (V16): alta completa, cabecera, preparación, despacho, entrega,
 * línea de tiempo, cambio de estado en lote, clientes recientes y numeración.
 */
@RestController
@RequestMapping("/sales/orders")
@Tag(name = "Orders")
public class OrderOperationsController {

    private final CreateFullOrderUseCase createFull;
    private final SaveOrderDetailsUseCase saveDetails;
    private final OrderFulfillmentUseCase fulfillment;
    private final OrderTimelineUseCase timeline;
    private final BulkChangeOrderStatusUseCase bulkStatus;
    private final OrderInsightsUseCase insights;

    public OrderOperationsController(CreateFullOrderUseCase createFull, SaveOrderDetailsUseCase saveDetails,
                                     OrderFulfillmentUseCase fulfillment, OrderTimelineUseCase timeline,
                                     BulkChangeOrderStatusUseCase bulkStatus, OrderInsightsUseCase insights) {
        this.createFull = createFull;
        this.saveDetails = saveDetails;
        this.fulfillment = fulfillment;
        this.timeline = timeline;
        this.bulkStatus = bulkStatus;
        this.insights = insights;
    }

    @Operation(summary = "Create full", description = "Orden con cliente, entrega, origen, descuento y productos en un paso (reserva el stock).")
    @PostMapping("/full")
    public ResponseEntity<OrderDto> createFull(@Valid @RequestBody CreateFullOrderDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createFull.createFull(input));
    }

    @Operation(summary = "Save details", description = "Cliente, entrega, origen, descuento y notas. Descuento, costo de envío y "
            + "bodega solo con la orden pendiente; la entrega hasta que se despacha.")
    @PutMapping("/{id}/details")
    public OrderDto saveDetails(@PathVariable Long id, @Valid @RequestBody SaveOrderDetailsDto input) {
        return saveDetails.saveDetails(id, input);
    }

    @Operation(summary = "Pick", description = "Unidades preparadas por línea; la primera pasa la orden de PAID a PROCESSING.")
    @PostMapping("/{id}/picking")
    public OrderDto pick(@PathVariable Long id, @Valid @RequestBody PickOrderDto input) {
        return fulfillment.pick(id, input);
    }

    @Operation(summary = "Pick all", description = "Marca todas las líneas como preparadas.")
    @PostMapping("/{id}/pick-all")
    public OrderDto pickAll(@PathVariable Long id, @Valid @RequestBody(required = false) OrderActionDto input) {
        return fulfillment.pickAll(id, input);
    }

    @Operation(summary = "Ready", description = "Lista para despachar o retirar (exige todo preparado).")
    @PostMapping("/{id}/ready")
    public OrderDto ready(@PathVariable Long id, @Valid @RequestBody(required = false) OrderActionDto input) {
        return fulfillment.ready(id, input);
    }

    @Operation(summary = "Ship", description = "Despacho con transportadora y guía (solo envíos; exige todo preparado).")
    @PostMapping("/{id}/ship")
    public OrderDto ship(@PathVariable Long id, @Valid @RequestBody ShipOrderDto input) {
        return fulfillment.ship(id, input);
    }

    @Operation(summary = "Deliver", description = "Entregada: envío despachado o retiro en tienda listo.")
    @PostMapping("/{id}/deliver")
    public OrderDto deliver(@PathVariable Long id, @Valid @RequestBody(required = false) OrderActionDto input) {
        return fulfillment.deliver(id, input);
    }

    @Operation(summary = "Timeline", description = "Eventos de la orden, del más antiguo al más reciente.")
    @GetMapping("/{id}/timeline")
    public List<OrderEventDto> timeline(@PathVariable Long id) {
        return timeline.timeline(id);
    }

    @Operation(summary = "Add note", description = "Nota interna en la línea de tiempo.")
    @PostMapping("/{id}/notes")
    public ResponseEntity<OrderEventDto> addNote(@PathVariable Long id, @Valid @RequestBody OrderActionDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timeline.addNote(id, input));
    }

    @Operation(summary = "Bulk status", description = "Cambia el estado de varias órdenes; los fallos se informan en 'failed'.")
    @PostMapping("/bulk-status")
    public BulkOperationResult bulkStatus(@Valid @RequestBody BulkOrderStatusDto input) {
        return bulkStatus.changeAll(input);
    }

    @Operation(summary = "Customers", description = "Clientes de órdenes anteriores (nombre, documento, correo o teléfono; mínimo 2 letras).")
    @GetMapping("/customers")
    public List<CustomerLookupDto> customers(@RequestParam(value = "q", required = false) String q) {
        return insights.customers(q);
    }

    @Operation(summary = "Customer by document", description = "Persona del registro central (access_control) por tipo y número de "
            + "documento, solo si es un usuario asignado a la empresa companyId (id_company de access_control), con el contacto y la "
            + "dirección de su última orden. central_status: FOUND, NOT_FOUND, UNAVAILABLE o DISABLED (sin credenciales o sin empresa).")
    @GetMapping("/customers/by-document")
    public com.essenza.draco.modules.sales.application.dto.order.CustomerByDocumentDto customerByDocument(
            @RequestParam(value = "docType", required = false) String docType,
            @RequestParam("docNumber") String docNumber,
            @RequestParam(value = "companyId", required = false) Long companyId) {
        return insights.customerByDocument(docType, docNumber, companyId);
    }

    @Operation(summary = "Numbering", description = "Prefijo, dígitos y siguiente número de las órdenes.")
    @GetMapping("/numbering")
    public OrderNumberingDto numbering() {
        return insights.numbering();
    }

    @Operation(summary = "Save numbering", description = "Cambia prefijo, dígitos o siguiente número (los números usados se saltan).")
    @PutMapping("/numbering")
    public OrderNumberingDto saveNumbering(@Valid @RequestBody OrderNumberingDto input) {
        return insights.saveNumbering(input);
    }
}
