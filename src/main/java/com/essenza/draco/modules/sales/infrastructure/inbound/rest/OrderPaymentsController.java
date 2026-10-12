package com.essenza.draco.modules.sales.infrastructure.inbound.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment.OrderPaymentsDto;
import com.essenza.draco.modules.sales.application.input.payment.FindOrderPaymentsUseCase;

@RestController
@RequestMapping("/sales/orders")
@Tag(name = "Orders")
@RequiredArgsConstructor
public class OrderPaymentsController {

    private final FindOrderPaymentsUseCase findOrderPayments;

    @Operation(summary = "Payments", description = "Cobro de la orden: total, pagado, pendiente, saldo y pagos.")
    @GetMapping("/{id}/payments")
    public OrderPaymentsDto payments(@PathVariable Long id) {
        return findOrderPayments.forOrder(id);
    }
}
