package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.inbound.rest;

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

import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.CreateCarrierUseCase;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.UpdateCarrierUseCase;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.DeleteCarrierUseCase;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.FindCarrierByIdUseCase;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.FindCarriersUseCase;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CreateCarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.UpdateCarrierDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product_distribution/carriers")
@Tag(name = "Carriers")
public class CarrierController {

    private final CreateCarrierUseCase createCarrier;
    private final UpdateCarrierUseCase updateCarrier;
    private final DeleteCarrierUseCase deleteCarrier;
    private final FindCarrierByIdUseCase findCarrierById;
    private final FindCarriersUseCase findCarriers;

    public CarrierController(CreateCarrierUseCase createCarrier,
                            UpdateCarrierUseCase updateCarrier,
                            DeleteCarrierUseCase deleteCarrier,
                            FindCarrierByIdUseCase findCarrierById,
                            FindCarriersUseCase findCarriers) {
        this.createCarrier = createCarrier;
        this.updateCarrier = updateCarrier;
        this.deleteCarrier = deleteCarrier;
        this.findCarrierById = findCarrierById;
        this.findCarriers = findCarriers;
    }

    @Operation(summary = "Create carrier", description = "Creates a new carrier and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Carrier created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarrierDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CarrierDto> create(
            @RequestBody(description = "Carrier to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateCarrierDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateCarrierDto input) {
        CarrierDto created = createCarrier.create(input);
        return ResponseEntity.created(URI.create("/carriers/" + created.getId())).body(created);
    }

    @Operation(summary = "Update carrier", description = "Updates an existing carrier by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrier updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarrierDto.class))),
            @ApiResponse(responseCode = "404", description = "Carrier not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CarrierDto> update(
            @Parameter(description = "Carrier ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Carrier data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateCarrierDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateCarrierDto input) {
        CarrierDto updated = updateCarrier.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get carrier by ID", description = "Returns a carrier by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrier found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarrierDto.class))),
            @ApiResponse(responseCode = "404", description = "Carrier not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CarrierDto> getById(@Parameter(description = "Carrier ID", required = true) @PathVariable Long id) {
        Optional<CarrierDto> result = findCarrierById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List carriers", description = "Returns all carriers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of carriers",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<CarrierDto> getAll() {
        return findCarriers.findAll();
    }

    @Operation(summary = "Delete carrier", description = "Deletes a carrier by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Carrier deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Carrier not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Carrier ID", required = true) @PathVariable Long id) {
        boolean deleted = deleteCarrier.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
