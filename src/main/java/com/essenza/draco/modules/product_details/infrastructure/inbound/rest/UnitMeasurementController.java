package com.essenza.draco.modules.product_details.infrastructure.inbound.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.essenza.draco.modules.product_details.application.input.unit_measurement.CreateUnitMeasurementUseCase;
import com.essenza.draco.modules.product_details.application.input.unit_measurement.UpdateUnitMeasurementUseCase;
import com.essenza.draco.modules.product_details.application.input.unit_measurement.DeleteUnitMeasurementUseCase;
import com.essenza.draco.modules.product_details.application.input.unit_measurement.FindUnitMeasurementByIdUseCase;
import com.essenza.draco.modules.product_details.application.input.unit_measurement.FindUnitMeasurementsUseCase;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UnitMeasurementDto;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.CreateUnitMeasurementDto;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UpdateUnitMeasurementDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product_details/unit_measurements")
@Tag(name = "Unit Measurement")
public class UnitMeasurementController {

    private final CreateUnitMeasurementUseCase createUnitMeasurement;
    private final UpdateUnitMeasurementUseCase updateUnitMeasurement;
    private final DeleteUnitMeasurementUseCase deleteUnitMeasurement;
    private final FindUnitMeasurementByIdUseCase findUnitMeasurementById;
    private final FindUnitMeasurementsUseCase findUnitMeasurements;

//    public UnitMeasurementController(CreateUnitMeasurementUseCase createUnitMeasurement,
//                                     UpdateUnitMeasurementUseCase updateUnitMeasurement,
//                                     DeleteUnitMeasurementUseCase deleteUnitMeasurement,
//                                     FindUnitMeasurementByIdUseCase findUnitMeasurementById,
//                                     FindUnitMeasurementsUseCase findUnitMeasurements) {
//        this.createUnitMeasurement = createUnitMeasurement;
//        this.updateUnitMeasurement = updateUnitMeasurement;
//        this.deleteUnitMeasurement = deleteUnitMeasurement;
//        this.findUnitMeasurementById = findUnitMeasurementById;
//        this.findUnitMeasurements = findUnitMeasurements;
//    }

    @Operation(summary = "Create unit measurement", description = "Creates a new unit measurement and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Unit measurement created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnitMeasurementDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UnitMeasurementDto> create(
            @RequestBody(description = "Unit measurement to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateUnitMeasurementDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateUnitMeasurementDto input) {
        UnitMeasurementDto created = createUnitMeasurement.create(input);
        return ResponseEntity.created(URI.create("/unit-measurements/" )).body(created);
    }

    @Operation(summary = "Update unit measurement", description = "Updates an existing unit measurement by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unit measurement updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnitMeasurementDto.class))),
            @ApiResponse(responseCode = "404", description = "Unit measurement not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UnitMeasurementDto> update(
            @Parameter(description = "Unit measurement ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Unit measurement data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateUnitMeasurementDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateUnitMeasurementDto input) {
        UnitMeasurementDto updated = updateUnitMeasurement.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get unit measurement by ID", description = "Returns a unit measurement by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unit measurement found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnitMeasurementDto.class))),
            @ApiResponse(responseCode = "404", description = "Unit measurement not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UnitMeasurementDto> getById(@Parameter(description = "Unit measurement ID", required = true) @PathVariable Long id) {
        Optional<UnitMeasurementDto> result = findUnitMeasurementById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List unit measurements", description = "Returns all unit measurements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of unit measurements",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<UnitMeasurementDto> getAll() {
        return findUnitMeasurements.findAll();
    }

    @Operation(summary = "Delete unit measurement", description = "Deletes a unit measurement by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Unit measurement deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Unit measurement not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Unit measurement ID", required = true) @PathVariable Long id) {
        Boolean deleted = deleteUnitMeasurement.deleteById(id);
        return Boolean.TRUE.equals(deleted) ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
