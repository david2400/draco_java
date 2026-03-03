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

import com.essenza.draco.modules.sales.application.input.payment_type.CreatePaymentTypeUseCase;
import com.essenza.draco.modules.sales.application.input.payment_type.UpdatePaymentTypeUseCase;
import com.essenza.draco.modules.sales.application.input.payment_type.DeletePaymentTypeUseCase;
import com.essenza.draco.modules.sales.application.input.payment_type.FindPaymentTypeByIdUseCase;
import com.essenza.draco.modules.sales.application.input.payment_type.FindPaymentTypesUseCase;
import com.essenza.draco.modules.sales.domain.dto.payment_type.PaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.CreatePaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.UpdatePaymentTypeDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payment-types")
@Tag(name = "Payment Type")
public class PaymentTypeController {

    private final CreatePaymentTypeUseCase createPaymentType;
    private final UpdatePaymentTypeUseCase updatePaymentType;
    private final DeletePaymentTypeUseCase deletePaymentType;
    private final FindPaymentTypeByIdUseCase findPaymentTypeById;
    private final FindPaymentTypesUseCase findPaymentTypes;

    public PaymentTypeController(CreatePaymentTypeUseCase createPaymentType,
                                 UpdatePaymentTypeUseCase updatePaymentType,
                                 DeletePaymentTypeUseCase deletePaymentType,
                                 FindPaymentTypeByIdUseCase findPaymentTypeById,
                                 FindPaymentTypesUseCase findPaymentTypes) {
        this.createPaymentType = createPaymentType;
        this.updatePaymentType = updatePaymentType;
        this.deletePaymentType = deletePaymentType;
        this.findPaymentTypeById = findPaymentTypeById;
        this.findPaymentTypes = findPaymentTypes;
    }

    @Operation(summary = "Create payment type", description = "Creates a new payment type and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment type created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentTypeDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PaymentTypeDto> create(
            @RequestBody(description = "Payment type to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreatePaymentTypeDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreatePaymentTypeDto input) {
        PaymentTypeDto created = createPaymentType.create(input);
        return ResponseEntity.created(URI.create("/payment-types/" )).body(created);
    }

    @Operation(summary = "Update payment type", description = "Updates an existing payment type by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment type updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentTypeDto.class))),
            @ApiResponse(responseCode = "404", description = "Payment type not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PaymentTypeDto> update(
            @Parameter(description = "Payment type ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Payment type data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdatePaymentTypeDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdatePaymentTypeDto input) {
        PaymentTypeDto updated = updatePaymentType.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get payment type by ID", description = "Returns a payment type by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment type found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentTypeDto.class))),
            @ApiResponse(responseCode = "404", description = "Payment type not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentTypeDto> getById(@Parameter(description = "Payment type ID", required = true) @PathVariable Long id) {
        Optional<PaymentTypeDto> result = findPaymentTypeById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List payment types", description = "Returns all payment types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of payment types",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<PaymentTypeDto> getAll() {
        return findPaymentTypes.findAll();
    }

    @Operation(summary = "Delete payment type", description = "Deletes a payment type by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment type deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment type not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Payment type ID", required = true) @PathVariable Long id) {
        boolean deleted = deletePaymentType.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
