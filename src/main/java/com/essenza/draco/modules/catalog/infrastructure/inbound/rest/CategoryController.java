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

import com.essenza.draco.modules.catalog.application.input.category.CreateCategoryUseCase;
import com.essenza.draco.modules.catalog.application.input.category.UpdateCategoryUseCase;
import com.essenza.draco.modules.catalog.application.input.category.DeleteCategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.category.FindCategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.category.FindAllCategoriesUseCase;
import com.essenza.draco.modules.catalog.domain.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.CreateCategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.UpdateCategoryDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories")
public class CategoryController {

    private final CreateCategoryUseCase createCategory;
    private final UpdateCategoryUseCase updateCategory;
    private final DeleteCategoryByIdUseCase deleteCategoryById;
    private final FindCategoryByIdUseCase findCategoryById;
    private final FindAllCategoriesUseCase findAllCategories;

    public CategoryController(CreateCategoryUseCase createCategory,
                              UpdateCategoryUseCase updateCategory,
                              DeleteCategoryByIdUseCase deleteCategoryById,
                              FindCategoryByIdUseCase findCategoryById,
                              FindAllCategoriesUseCase findAllCategories) {
        this.createCategory = createCategory;
        this.updateCategory = updateCategory;
        this.deleteCategoryById = deleteCategoryById;
        this.findCategoryById = findCategoryById;
        this.findAllCategories = findAllCategories;
    }

    @Operation(summary = "Create category", description = "Creates a new category and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoryDto> create(
            @RequestBody(description = "Category to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateCategoryDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateCategoryDto input) {
        CategoryDto created = createCategory.create(input);
        return ResponseEntity.created(URI.create("/categories/")).body(created);
    }

    @Operation(summary = "Update category", description = "Updates an existing category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(
            @Parameter(description = "Category ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Category data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateCategoryDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateCategoryDto input) {
        CategoryDto updated = updateCategory.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get category by ID", description = "Returns a category by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@Parameter(description = "Category ID", required = true) @PathVariable Long id) {
        Optional<CategoryDto> result = findCategoryById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List categories", description = "Returns all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<CategoryDto> getAll() {
        return findAllCategories.findAll();
    }

    @Operation(summary = "Delete category", description = "Deletes a category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Category ID", required = true) @PathVariable Long id) {
        deleteCategoryById.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
