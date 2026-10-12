package com.essenza.draco.modules.sales.infrastructure.inbound.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment_method.GatewayProviderDto;
import com.essenza.draco.modules.sales.application.dto.payment_method.PaymentMethodDto;
import com.essenza.draco.modules.sales.application.dto.payment_method.SavePaymentMethodDto;
import com.essenza.draco.modules.sales.application.input.payment_method.CreatePaymentMethodUseCase;
import com.essenza.draco.modules.sales.application.input.payment_method.DeletePaymentMethodUseCase;
import com.essenza.draco.modules.sales.application.input.payment_method.FindGatewayProvidersUseCase;
import com.essenza.draco.modules.sales.application.input.payment_method.FindPaymentMethodByIdUseCase;
import com.essenza.draco.modules.sales.application.input.payment_method.FindPaymentMethodsUseCase;
import com.essenza.draco.modules.sales.application.input.payment_method.UpdatePaymentMethodUseCase;
import com.essenza.draco.shared.exceptions.NotFoundException;

@RestController
@RequestMapping("/sales/payment_methods")
@Tag(name = "Payment methods", description = "Medios de pago físicos y virtuales (efectivo, datáfono, transferencia, pasarelas)")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final FindPaymentMethodsUseCase findMethods;
    private final FindPaymentMethodByIdUseCase findMethod;
    private final CreatePaymentMethodUseCase createMethod;
    private final UpdatePaymentMethodUseCase updateMethod;
    private final DeletePaymentMethodUseCase deleteMethod;
    private final FindGatewayProvidersUseCase findProviders;

    @Operation(summary = "List", description = "Medios ordenados por posición. includeInactive=true para el panel; channel=PHYSICAL|VIRTUAL.")
    @GetMapping
    public List<PaymentMethodDto> list(@RequestParam(value = "includeInactive", defaultValue = "false") boolean includeInactive,
                                       @RequestParam(value = "channel", required = false) String channel) {
        return findMethods.findAll(includeInactive, channel);
    }

    @Operation(summary = "Gateways", description = "Pasarelas disponibles, si tienen credenciales y la URL del webhook a registrar.")
    @GetMapping("/providers")
    public List<GatewayProviderDto> providers() {
        return findProviders.providers();
    }

    @Operation(summary = "Get by id")
    @GetMapping("/{id}")
    public PaymentMethodDto get(@PathVariable Long id) {
        return findMethod.findById(id).orElseThrow(() -> new NotFoundException("Medio de pago no encontrado: " + id));
    }

    @Operation(summary = "Create")
    @PostMapping
    public ResponseEntity<PaymentMethodDto> create(@Valid @RequestBody SavePaymentMethodDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createMethod.create(input));
    }

    @Operation(summary = "Update")
    @PutMapping("/{id}")
    public PaymentMethodDto update(@PathVariable Long id, @Valid @RequestBody SavePaymentMethodDto input) {
        return updateMethod.update(id, input);
    }

    @Operation(summary = "Delete", description = "409 si ya tiene pagos: desactívalo en su lugar.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteMethod.delete(id);
        return ResponseEntity.noContent().build();
    }
}
