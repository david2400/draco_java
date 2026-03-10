package com.essenza.draco.modules.devolution.infrastructure.inbound.rest;

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

import com.essenza.draco.modules.devolution.application.input.order_devolution.CreateOrderDevolutionUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution.UpdateOrderDevolutionUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution.DeleteOrderDevolutionUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution.FindOrderDevolutionByIdUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution.FindOrderDevolutionsUseCase;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.CreateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.UpdateOrderDevolutionDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/devolution/order_devolutions")
@Tag(name = "Order Devolutions")
public class OrderDevolutionController {

    private final CreateOrderDevolutionUseCase createOrderDevolution;
    private final UpdateOrderDevolutionUseCase updateOrderDevolution;
    private final DeleteOrderDevolutionUseCase deleteOrderDevolution;
    private final FindOrderDevolutionByIdUseCase findOrderDevolutionById;
    private final FindOrderDevolutionsUseCase findOrderDevolutions;

    public OrderDevolutionController(CreateOrderDevolutionUseCase createOrderDevolution,
                                     UpdateOrderDevolutionUseCase updateOrderDevolution,
                                     DeleteOrderDevolutionUseCase deleteOrderDevolution,
                                     FindOrderDevolutionByIdUseCase findOrderDevolutionById,
                                     FindOrderDevolutionsUseCase findOrderDevolutions) {
        this.createOrderDevolution = createOrderDevolution;
        this.updateOrderDevolution = updateOrderDevolution;
        this.deleteOrderDevolution = deleteOrderDevolution;
        this.findOrderDevolutionById = findOrderDevolutionById;
        this.findOrderDevolutions = findOrderDevolutions;
    }

    @Operation(summary = "Create order devolution", description = "Creates a new order devolution and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order devolution created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDevolutionDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderDevolutionDto> create(
            @RequestBody(description = "Order devolution to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateOrderDevolutionDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateOrderDevolutionDto input) {
        OrderDevolutionDto created = createOrderDevolution.create(input);
        return ResponseEntity.created(URI.create("/order-devolutions/" + created.getId())).body(created);
    }

    @Operation(summary = "Update order devolution", description = "Updates an existing order devolution by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order devolution updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDevolutionDto.class))),
            @ApiResponse(responseCode = "404", description = "Order devolution not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderDevolutionDto> update(
            @Parameter(description = "Order devolution ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Order devolution data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateOrderDevolutionDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateOrderDevolutionDto input) {
        OrderDevolutionDto updated = updateOrderDevolution.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get order devolution by ID", description = "Returns an order devolution by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order devolution found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDevolutionDto.class))),
            @ApiResponse(responseCode = "404", description = "Order devolution not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDevolutionDto> getById(@Parameter(description = "Order devolution ID", required = true) @PathVariable Long id) {
        Optional<OrderDevolutionDto> result = findOrderDevolutionById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List order devolutions", description = "Returns all order devolutions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of order devolutions",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<OrderDevolutionDto> getAll() {
        return findOrderDevolutions.findAll();
    }

    @Operation(summary = "Delete order devolution", description = "Deletes an order devolution by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order devolution deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order devolution not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Order devolution ID", required = true) @PathVariable Long id) {
        boolean deleted = deleteOrderDevolution.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
