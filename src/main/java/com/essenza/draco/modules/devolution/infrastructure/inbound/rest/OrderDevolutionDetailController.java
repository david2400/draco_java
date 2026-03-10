package com.essenza.draco.modules.devolution.infrastructure.inbound.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.CreateOrderDevolutionDetailUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.UpdateOrderDevolutionDetailUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.DeleteOrderDevolutionDetailUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.FindOrderDevolutionDetailByIdUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.FindOrderDevolutionDetailsUseCase;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.OrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.CreateOrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.UpdateOrderDevolutionDetailDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/devolution/order_devolution_details")
@Tag(name = "Order Devolutions Details")
public class OrderDevolutionDetailController {

    private final CreateOrderDevolutionDetailUseCase createOrderDevolutionDetail;
    private final UpdateOrderDevolutionDetailUseCase updateOrderDevolutionDetail;
    private final DeleteOrderDevolutionDetailUseCase deleteOrderDevolutionDetail;
    private final FindOrderDevolutionDetailByIdUseCase findOrderDevolutionDetailById;
    private final FindOrderDevolutionDetailsUseCase findOrderDevolutionDetails;

    public OrderDevolutionDetailController(CreateOrderDevolutionDetailUseCase createOrderDevolutionDetail,
                                           UpdateOrderDevolutionDetailUseCase updateOrderDevolutionDetail,
                                           DeleteOrderDevolutionDetailUseCase deleteOrderDevolutionDetail,
                                           FindOrderDevolutionDetailByIdUseCase findOrderDevolutionDetailById,
                                           FindOrderDevolutionDetailsUseCase findOrderDevolutionDetails) {
        this.createOrderDevolutionDetail = createOrderDevolutionDetail;
        this.updateOrderDevolutionDetail = updateOrderDevolutionDetail;
        this.deleteOrderDevolutionDetail = deleteOrderDevolutionDetail;
        this.findOrderDevolutionDetailById = findOrderDevolutionDetailById;
        this.findOrderDevolutionDetails = findOrderDevolutionDetails;
    }

    @Operation(summary = "Create order devolution detail", description = "Creates a new order devolution detail and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order devolution detail created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDevolutionDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderDevolutionDetailDto> create(
            @RequestBody(description = "Order devolution detail to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateOrderDevolutionDetailDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateOrderDevolutionDetailDto input) {
        OrderDevolutionDetailDto created = createOrderDevolutionDetail.create(input);
        return ResponseEntity.created(URI.create("/order-devolution-details/" )).body(created);
    }

    @Operation(summary = "Update order devolution detail", description = "Updates an existing order devolution detail by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order devolution detail updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDevolutionDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "Order devolution detail not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderDevolutionDetailDto> update(
            @Parameter(description = "Order devolution detail ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Order devolution detail data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateOrderDevolutionDetailDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateOrderDevolutionDetailDto input) {
        OrderDevolutionDetailDto updated = updateOrderDevolutionDetail.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get order devolution detail by ID", description = "Returns an order devolution detail by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order devolution detail found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDevolutionDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "Order devolution detail not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDevolutionDetailDto> getById(@Parameter(description = "Order devolution detail ID", required = true) @PathVariable Long id) {
        Optional<OrderDevolutionDetailDto> result = findOrderDevolutionDetailById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List order devolution details", description = "Returns all order devolution details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of order devolution details",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<OrderDevolutionDetailDto> getAll() {
        return findOrderDevolutionDetails.findAll();
    }

    @Operation(summary = "Delete order devolution detail", description = "Deletes an order devolution detail by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order devolution detail deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order devolution detail not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Order devolution detail ID", required = true) @PathVariable Long id) {
        boolean deleted = deleteOrderDevolutionDetail.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
