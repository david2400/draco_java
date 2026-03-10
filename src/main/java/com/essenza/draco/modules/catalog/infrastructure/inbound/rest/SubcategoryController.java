package com.essenza.draco.modules.catalog.infrastructure.inbound.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
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

import com.essenza.draco.modules.catalog.application.input.subcategory.CreateSubcategoryUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.UpdateSubcategoryUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.DeleteSubcategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.FindSubcategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.FindAllSubcategoriesUseCase;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.UpdateSubcategoryDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/catalog/subcategories")
@Tag(name = "Subcategories")
public class SubcategoryController {

    private final CreateSubcategoryUseCase createSubcategory;
    private final UpdateSubcategoryUseCase updateSubcategory;
    private final DeleteSubcategoryByIdUseCase deleteSubcategoryById;
    private final FindSubcategoryByIdUseCase findSubcategoryById;
    private final FindAllSubcategoriesUseCase findAllSubcategories;

    public SubcategoryController(CreateSubcategoryUseCase createSubcategory,
                                 UpdateSubcategoryUseCase updateSubcategory,
                                 DeleteSubcategoryByIdUseCase deleteSubcategoryById,
                                 FindSubcategoryByIdUseCase findSubcategoryById,
                                 FindAllSubcategoriesUseCase findAllSubcategories) {
        this.createSubcategory = createSubcategory;
        this.updateSubcategory = updateSubcategory;
        this.deleteSubcategoryById = deleteSubcategoryById;
        this.findSubcategoryById = findSubcategoryById;
        this.findAllSubcategories = findAllSubcategories;
    }

    @Operation(summary = "Create subcategory", description = "Creates a new subcategory and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subcategory created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubcategoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<SubcategoryDto> create(
            @RequestBody(description = "Subcategory to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateSubcategoryDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateSubcategoryDto input) {
        SubcategoryDto created = createSubcategory.create(input);
        return ResponseEntity.created(URI.create("/subcategories/")).body(created);
    }

    @Operation(summary = "Update subcategory", description = "Updates an existing subcategory by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subcategory updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubcategoryDto.class))),
            @ApiResponse(responseCode = "404", description = "Subcategory not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<SubcategoryDto> update(
            @Parameter(description = "Subcategory ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Subcategory data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateSubcategoryDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateSubcategoryDto input) {
        SubcategoryDto updated = updateSubcategory.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get subcategory by ID", description = "Returns a subcategory by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subcategory found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubcategoryDto.class))),
            @ApiResponse(responseCode = "404", description = "Subcategory not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SubcategoryDto> getById(@Parameter(description = "Subcategory ID", required = true) @PathVariable Long id) {
        Optional<SubcategoryDto> result = findSubcategoryById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List subcategories", description = "Returns all subcategories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of subcategories",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<SubcategoryDto> getAll() {
        return findAllSubcategories.findAll();
    }

    @Operation(summary = "Delete subcategory", description = "Deletes a subcategory by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subcategory deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Subcategory not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Subcategory ID", required = true) @PathVariable Long id) {
        deleteSubcategoryById.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
