package com.essenza.draco.modules.devolution.infrastructure.inbound.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.essenza.draco.modules.devolution.application.input.motive_devolution.CreateMotiveDevolutionUseCase;
import com.essenza.draco.modules.devolution.application.input.motive_devolution.UpdateMotiveDevolutionUseCase;
import com.essenza.draco.modules.devolution.application.input.motive_devolution.DeleteMotiveDevolutionUseCase;
import com.essenza.draco.modules.devolution.application.input.motive_devolution.FindMotiveDevolutionByIdUseCase;
import com.essenza.draco.modules.devolution.application.input.motive_devolution.FindMotiveDevolutionsUseCase;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.MotiveDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.CreateMotiveDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.UpdateMotiveDevolutionDto;

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
@RequestMapping("/motive-devolutions")
@Tag(name = "Motive Devolutions")
public class MotiveDevolutionController {

    private final CreateMotiveDevolutionUseCase createMotiveDevolution;
    private final UpdateMotiveDevolutionUseCase updateMotiveDevolution;
    private final DeleteMotiveDevolutionUseCase deleteMotiveDevolution;
    private final FindMotiveDevolutionByIdUseCase findMotiveDevolutionById;
    private final FindMotiveDevolutionsUseCase findMotiveDevolutions;

    public MotiveDevolutionController(CreateMotiveDevolutionUseCase createMotiveDevolution,
                                      UpdateMotiveDevolutionUseCase updateMotiveDevolution,
                                      DeleteMotiveDevolutionUseCase deleteMotiveDevolution,
                                      FindMotiveDevolutionByIdUseCase findMotiveDevolutionById,
                                      FindMotiveDevolutionsUseCase findMotiveDevolutions) {
        this.createMotiveDevolution = createMotiveDevolution;
        this.updateMotiveDevolution = updateMotiveDevolution;
        this.deleteMotiveDevolution = deleteMotiveDevolution;
        this.findMotiveDevolutionById = findMotiveDevolutionById;
        this.findMotiveDevolutions = findMotiveDevolutions;
    }

    @Operation(summary = "Create motive devolution", description = "Creates a new motive devolution and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Motive devolution created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MotiveDevolutionDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<MotiveDevolutionDto> create(
            @RequestBody(description = "Motive devolution to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateMotiveDevolutionDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateMotiveDevolutionDto input) {
        MotiveDevolutionDto created = createMotiveDevolution.create(input);
        return ResponseEntity.created(URI.create("/motive-devolutions/" )).body(created);
    }

    @Operation(summary = "Update motive devolution", description = "Updates an existing motive devolution by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motive devolution updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MotiveDevolutionDto.class))),
            @ApiResponse(responseCode = "404", description = "Motive devolution not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<MotiveDevolutionDto> update(
            @Parameter(description = "Motive devolution ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Motive devolution data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateMotiveDevolutionDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateMotiveDevolutionDto input) {
        MotiveDevolutionDto updated = updateMotiveDevolution.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get motive devolution by ID", description = "Returns a motive devolution by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motive devolution found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MotiveDevolutionDto.class))),
            @ApiResponse(responseCode = "404", description = "Motive devolution not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<MotiveDevolutionDto> getById(@Parameter(description = "Motive devolution ID", required = true) @PathVariable Long id) {
        Optional<MotiveDevolutionDto> result = findMotiveDevolutionById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List motive devolutions", description = "Returns all motive devolutions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of motive devolutions",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<MotiveDevolutionDto> getAll() {
        return findMotiveDevolutions.findAll();
    }

    @Operation(summary = "Delete motive devolution", description = "Deletes a motive devolution by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Motive devolution deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Motive devolution not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Motive devolution ID", required = true) @PathVariable Long id) {
        boolean deleted = deleteMotiveDevolution.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
