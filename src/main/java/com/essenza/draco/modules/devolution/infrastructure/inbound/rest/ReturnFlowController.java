package com.essenza.draco.modules.devolution.infrastructure.inbound.rest;

import java.net.URI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.essenza.draco.modules.devolution.application.dto.flow.ApproveReturnDto;
import com.essenza.draco.modules.devolution.application.dto.flow.InspectReturnDto;
import com.essenza.draco.modules.devolution.application.dto.flow.ReceiveReturnDto;
import com.essenza.draco.modules.devolution.application.dto.flow.RefundReturnDto;
import com.essenza.draco.modules.devolution.application.dto.flow.RejectReturnDto;
import com.essenza.draco.modules.devolution.application.dto.flow.ReturnRequestDto;
import com.essenza.draco.modules.devolution.application.dto.flow.ReturnableOrderDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.input.flow.ApproveReturnUseCase;
import com.essenza.draco.modules.devolution.application.input.flow.FindReturnableUseCase;
import com.essenza.draco.modules.devolution.application.input.flow.InspectReturnUseCase;
import com.essenza.draco.modules.devolution.application.input.flow.ReceiveReturnUseCase;
import com.essenza.draco.modules.devolution.application.input.flow.RefundReturnUseCase;
import com.essenza.draco.modules.devolution.application.input.flow.RejectReturnUseCase;
import com.essenza.draco.modules.devolution.application.input.flow.RequestReturnUseCase;

/** Flujo de devoluciones conectado a stock, pagos y facturación. */
@RestController
@Tag(name = "Order Devolutions", description = "Flujo: solicitar → aprobar → recibir → inspeccionar → reembolsar")
public class ReturnFlowController {

    private final RequestReturnUseCase requestReturn;
    private final ApproveReturnUseCase approveReturn;
    private final RejectReturnUseCase rejectReturn;
    private final ReceiveReturnUseCase receiveReturn;
    private final InspectReturnUseCase inspectReturn;
    private final RefundReturnUseCase refundReturn;
    private final FindReturnableUseCase findReturnable;

    public ReturnFlowController(RequestReturnUseCase requestReturn, ApproveReturnUseCase approveReturn,
                                RejectReturnUseCase rejectReturn, ReceiveReturnUseCase receiveReturn,
                                InspectReturnUseCase inspectReturn, RefundReturnUseCase refundReturn,
                                FindReturnableUseCase findReturnable) {
        this.requestReturn = requestReturn;
        this.approveReturn = approveReturn;
        this.rejectReturn = rejectReturn;
        this.receiveReturn = receiveReturn;
        this.inspectReturn = inspectReturn;
        this.refundReturn = refundReturn;
        this.findReturnable = findReturnable;
    }

    @Operation(summary = "Lo que se puede devolver de una orden",
            description = "Líneas con lo vendido, lo ya devuelto y lo disponible; además lo reembolsable de los pagos.")
    @GetMapping("/devolution/orders/{orderId}/returnable")
    public ReturnableOrderDto returnable(@PathVariable Long orderId,
                                         @RequestParam(required = false) Long excludeDevolutionId) {
        return findReturnable.forOrder(orderId, excludeDevolutionId);
    }

    @Operation(summary = "Solicitar devolución", description = "Crea la devolución pendiente con sus líneas (precio de la orden).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Devolución creada"),
            @ApiResponse(responseCode = "400", description = "Cantidades o datos inválidos"),
            @ApiResponse(responseCode = "409", description = "La orden no admite devoluciones")})
    @PostMapping("/devolution/order_devolutions/request")
    public ResponseEntity<OrderDevolutionDto> request(@Valid @RequestBody ReturnRequestDto input) {
        OrderDevolutionDto created = requestReturn.request(input);
        return ResponseEntity.created(URI.create("/devolution/order_devolutions/" + created.getId())).body(created);
    }

    @Operation(summary = "Aprobar devolución (P → A)")
    @PostMapping("/devolution/order_devolutions/{id}/approve")
    public OrderDevolutionDto approve(@PathVariable Long id, @Valid @RequestBody(required = false) ApproveReturnDto input) {
        return approveReturn.approve(id, input);
    }

    @Operation(summary = "Rechazar devolución (P/A/R → X)")
    @PostMapping("/devolution/order_devolutions/{id}/reject")
    public OrderDevolutionDto reject(@PathVariable Long id, @Valid @RequestBody RejectReturnDto input) {
        return rejectReturn.reject(id, input);
    }

    @Operation(summary = "Registrar recepción (A → R)", description = "Unidades recibidas por línea; las omitidas se reciben completas.")
    @PostMapping("/devolution/order_devolutions/{id}/receive")
    public OrderDevolutionDto receive(@PathVariable Long id, @Valid @RequestBody(required = false) ReceiveReturnDto input) {
        return receiveReturn.receive(id, input);
    }

    @Operation(summary = "Inspeccionar (R → I)",
            description = "Estado, reingreso y cargo por línea; reingresa al stock y calcula el valor acreditado.")
    @PostMapping("/devolution/order_devolutions/{id}/inspect")
    public OrderDevolutionDto inspect(@PathVariable Long id, @Valid @RequestBody InspectReturnDto input) {
        return inspectReturn.inspect(id, input);
    }

    @Operation(summary = "Reembolsar (I → F)",
            description = "Reembolsa sobre los pagos de la orden y emite la nota crédito parcial si la orden tiene factura.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reembolsada"),
            @ApiResponse(responseCode = "409", description = "Los pagos no alcanzan para el monto pedido"),
            @ApiResponse(responseCode = "500", description = "La pasarela rechazó el reembolso (no se guarda nada)")})
    @PostMapping("/devolution/order_devolutions/{id}/refund")
    public OrderDevolutionDto refund(@PathVariable Long id, @Valid @RequestBody(required = false) RefundReturnDto input) {
        return refundReturn.refund(id, input);
    }
}
