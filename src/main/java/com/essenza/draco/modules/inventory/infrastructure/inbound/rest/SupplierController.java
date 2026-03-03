package com.essenza.draco.modules.inventory.infrastructure.inbound.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.essenza.draco.modules.inventory.application.input.supplier.CreateSupplierUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.UpdateSupplierUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.DeleteSupplierUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.FindSupplierByIdUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.FindSuppliersUseCase;
import com.essenza.draco.modules.inventory.domain.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.CreateSupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.UpdateSupplierDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final CreateSupplierUseCase createSupplier;
    private final UpdateSupplierUseCase updateSupplier;
    private final DeleteSupplierUseCase deleteSupplier;
    private final FindSupplierByIdUseCase findSupplierById;
    private final FindSuppliersUseCase findSuppliers;

    public SupplierController(CreateSupplierUseCase createSupplier,
                              UpdateSupplierUseCase updateSupplier,
                              DeleteSupplierUseCase deleteSupplier,
                              FindSupplierByIdUseCase findSupplierById,
                              FindSuppliersUseCase findSuppliers) {
        this.createSupplier = createSupplier;
        this.updateSupplier = updateSupplier;
        this.deleteSupplier = deleteSupplier;
        this.findSupplierById = findSupplierById;
        this.findSuppliers = findSuppliers;
    }

    @Operation(summary = "Create supplier", description = "Creates a new supplier and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supplier created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<SupplierDto> create(
            @RequestBody(description = "Supplier to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateSupplierDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateSupplierDto input) {
        SupplierDto created = createSupplier.create(input);
        return ResponseEntity.created(URI.create("/suppliers/" )).body(created);
    }

    @Operation(summary = "Update supplier", description = "Updates an existing supplier by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierDto.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<SupplierDto> update(
            @Parameter(description = "Supplier ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Supplier data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateSupplierDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateSupplierDto input) {
        SupplierDto updated = updateSupplier.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get supplier by ID", description = "Returns a supplier by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierDto.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDto> getById(@Parameter(description = "Supplier ID", required = true) @PathVariable Long id) {
        Optional<SupplierDto> result = findSupplierById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List suppliers", description = "Returns all suppliers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of suppliers",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<SupplierDto> getAll() {
        return findSuppliers.findAll();
    }

    @Operation(summary = "Delete supplier", description = "Deletes a supplier by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supplier deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Supplier ID", required = true) @PathVariable Long id) {
        Boolean deleted = deleteSupplier.deleteById(id);
        return Boolean.TRUE.equals(deleted) ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
