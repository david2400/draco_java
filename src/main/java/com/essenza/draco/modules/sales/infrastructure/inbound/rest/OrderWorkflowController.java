package com.essenza.draco.modules.sales.infrastructure.inbound.rest;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.essenza.draco.modules.sales.application.dto.order.ChangeOrderStatusDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderReservationDto;
import com.essenza.draco.modules.sales.application.input.order.ChangeOrderStatusUseCase;
import com.essenza.draco.modules.sales.application.input.order.FindOrderReservationsUseCase;

@RestController
@RequestMapping("/sales/orders")
@Tag(name = "Orders")
public class OrderWorkflowController {

    private final ChangeOrderStatusUseCase changeStatus;
    private final FindOrderReservationsUseCase findReservations;

    public OrderWorkflowController(ChangeOrderStatusUseCase changeStatus, FindOrderReservationsUseCase findReservations) {
        this.changeStatus = changeStatus;
        this.findReservations = findReservations;
    }

    @Operation(summary = "Change order status",
            description = "PENDING → PAID descuenta el stock reservado; → CANCELLED libera la reserva (pendiente) o "
                    + "devuelve el stock (pagada o en proceso). 409 si la transición no está permitida.")
    @PostMapping("/{id}/status")
    public OrderDto changeStatus(@PathVariable Long id, @Valid @RequestBody ChangeOrderStatusDto input) {
        return changeStatus.changeStatus(id, input);
    }

    @Operation(summary = "Order stock reservations", description = "Reservas de stock por línea y bodega, con su estado.")
    @GetMapping("/{id}/reservations")
    public List<OrderReservationDto> reservations(@PathVariable Long id) {
        return findReservations.reservations(id);
    }
}
