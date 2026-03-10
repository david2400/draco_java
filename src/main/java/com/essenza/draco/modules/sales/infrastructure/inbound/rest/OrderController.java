package com.essenza.draco.modules.sales.infrastructure.inbound.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.essenza.draco.modules.sales.application.input.order.CreateOrderUseCase;
import com.essenza.draco.modules.sales.application.input.order.UpdateOrderUseCase;
import com.essenza.draco.modules.sales.application.input.order.DeleteOrderUseCase;
import com.essenza.draco.modules.sales.application.input.order.FindOrderByIdUseCase;
import com.essenza.draco.modules.sales.application.input.order.FindOrdersUseCase;
import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;
import com.essenza.draco.modules.sales.domain.dto.order.CreateOrderDto;
import com.essenza.draco.modules.sales.domain.dto.order.UpdateOrderDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sales/orders")
@Tag(name = "Orders")
public class OrderController {

    private final CreateOrderUseCase createOrder;
    private final UpdateOrderUseCase updateOrder;
    private final DeleteOrderUseCase deleteOrder;
    private final FindOrderByIdUseCase findOrderById;
    private final FindOrdersUseCase findOrders;

    public OrderController(CreateOrderUseCase createOrder,
                           UpdateOrderUseCase updateOrder,
                           DeleteOrderUseCase deleteOrder,
                           FindOrderByIdUseCase findOrderById,
                           FindOrdersUseCase findOrders) {
        this.createOrder = createOrder;
        this.updateOrder = updateOrder;
        this.deleteOrder = deleteOrder;
        this.findOrderById = findOrderById;
        this.findOrders = findOrders;
    }

    @Operation(summary = "Create order", description = "Creates a new order and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderDto> create(
            @RequestBody(description = "Order to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateOrderDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateOrderDto input) {
        OrderDto created = createOrder.create(input);
        return ResponseEntity.created(URI.create("/orders/" )).body(created);
    }

    @Operation(summary = "Update order", description = "Updates an existing order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Order data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateOrderDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateOrderDto input) {
        OrderDto updated = updateOrder.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete order", description = "Deletes an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Order ID", required = true) @PathVariable Long id) {
        boolean deleted = deleteOrder.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Get order by ID", description = "Returns an order by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@Parameter(description = "Order ID", required = true) @PathVariable Long id) {
        Optional<OrderDto> result = findOrderById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List orders", description = "Returns all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<OrderDto> getAll() {
        return findOrders.findAll();
    }
}
