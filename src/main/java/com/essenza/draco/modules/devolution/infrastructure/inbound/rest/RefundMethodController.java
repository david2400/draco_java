package com.essenza.draco.modules.devolution.infrastructure.inbound.rest;

import com.essenza.draco.modules.devolution.application.input.refund_method.*;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.CreateRefundMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.RefundMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.UpdateRefundMethodDto;
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

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/devolution/refund_methods")
@Tag(name = "Refund Methods")
public class RefundMethodController {

    private final CreateRefundMethodUseCase createRefundMethod;
    private final UpdateRefundMethodUseCase updateRefundMethod;
    private final DeleteRefundMethodUseCase deleteRefundMethod;
    private final FindRefundMethodByIdUseCase findRefundMethodById;
    private final FindRefundMethodsUseCase findRefundMethods;

    public RefundMethodController(CreateRefundMethodUseCase createRefundMethod,
                                  UpdateRefundMethodUseCase updateRefundMethod,
                                  DeleteRefundMethodUseCase deleteRefundMethod,
                                  FindRefundMethodByIdUseCase findRefundMethodById,
                                  FindRefundMethodsUseCase findRefundMethods) {
        this.createRefundMethod = createRefundMethod;
        this.updateRefundMethod = updateRefundMethod;
        this.deleteRefundMethod = deleteRefundMethod;
        this.findRefundMethodById = findRefundMethodById;
        this.findRefundMethods = findRefundMethods;
    }

    @Operation(summary = "Create refund method", description = "Creates a new refund method and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Refund method created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefundMethodDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RefundMethodDto> create(
            @RequestBody(description = "Refund method to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateRefundMethodDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateRefundMethodDto input) {
        RefundMethodDto created = createRefundMethod.create(input);
        return ResponseEntity.created(URI.create("/refund-methods/" + created.getId())).body(created);
    }

    @Operation(summary = "Update refund method", description = "Updates an existing refund method by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refund method updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefundMethodDto.class))),
            @ApiResponse(responseCode = "404", description = "Refund method not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<RefundMethodDto> update(
            @Parameter(description = "Refund method ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Refund method data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateRefundMethodDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateRefundMethodDto input) {
        RefundMethodDto updated = updateRefundMethod.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get refund method by ID", description = "Returns a refund method by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refund method found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefundMethodDto.class))),
            @ApiResponse(responseCode = "404", description = "Refund method not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RefundMethodDto> getById(@Parameter(description = "Refund method ID", required = true) @PathVariable Long id) {
        Optional<RefundMethodDto> result = findRefundMethodById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List refund methods", description = "Returns all refund methods")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of refund methods",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<RefundMethodDto> getAll() {
        return findRefundMethods.findAll();
    }

    @Operation(summary = "Delete refund method", description = "Deletes a refund method by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Refund method deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Refund method not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Refund method ID", required = true) @PathVariable Long id) {
        boolean deleted = deleteRefundMethod.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
