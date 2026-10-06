package com.essenza.draco.modules.catalog.infrastructure.inbound.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.essenza.draco.shared.common.domain.dto.BulkIdsRequest;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.shared.common.web.PageableFactory;
import com.essenza.draco.modules.catalog.application.input.category.BulkDeleteCategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.category.CreateCategoryUseCase;
import com.essenza.draco.modules.catalog.application.input.category.DeleteCategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.category.FindAllCategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.category.FindCategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.category.SearchCategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.category.UpdateCategoryUseCase;
import com.essenza.draco.modules.catalog.application.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.application.dto.category.CreateCategoryDto;
import com.essenza.draco.modules.catalog.application.dto.category.UpdateCategoryDto;

@RestController
@RequestMapping("/catalog/categories")
@Tag(name = "Categories")
public class CategoryController {

    /** Campos por los que se permite ordenar (lista blanca). */
    private static final Set<String> SORTABLE = Set.of("id", "name", "slug", "createdAt", "updatedAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("name").ignoreCase());

    private final CreateCategoryUseCase createCategory;
    private final UpdateCategoryUseCase updateCategory;
    private final DeleteCategoryByIdUseCase deleteCategoryById;
    private final FindCategoryByIdUseCase findCategoryById;
    private final FindAllCategoriesUseCase findAllCategories;
    private final SearchCategoriesUseCase searchCategories;
    private final BulkDeleteCategoriesUseCase bulkDeleteCategories;
    private final Validator validator;

    public CategoryController(CreateCategoryUseCase createCategory,
                         UpdateCategoryUseCase updateCategory,
                         DeleteCategoryByIdUseCase deleteCategoryById,
                         FindCategoryByIdUseCase findCategoryById,
                         FindAllCategoriesUseCase findAllCategories,
                         SearchCategoriesUseCase searchCategories,
                         BulkDeleteCategoriesUseCase bulkDeleteCategories,
                         Validator validator) {
        this.createCategory = createCategory;
        this.updateCategory = updateCategory;
        this.deleteCategoryById = deleteCategoryById;
        this.findCategoryById = findCategoryById;
        this.findAllCategories = findAllCategories;
        this.searchCategories = searchCategories;
        this.bulkDeleteCategories = bulkDeleteCategories;
        this.validator = validator;
    }

    @Operation(summary = "Create category", description = "Crea el registro. El slug es opcional (se genera desde el nombre).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Duplicated name or slug", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CreateCategoryDto input) {
        CategoryDto created = createCategory.create(input);
        return ResponseEntity.created(URI.create("/catalog/categories/" + created.getId())).body(created);
    }

    @Operation(summary = "Update category", description = "Actualiza el registro. El id se toma de la URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Duplicated name or slug", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@Parameter(description = "ID", required = true) @PathVariable Long id,
                                         @RequestBody UpdateCategoryDto input) {
        // El id del cuerpo es opcional: se fija desde la URL y luego se valida.
        input.setId(id);
        Set<ConstraintViolation<UpdateCategoryDto>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return ResponseEntity.ok(updateCategory.update(id, input));
    }

    @Operation(summary = "Get category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@Parameter(description = "ID", required = true) @PathVariable Long id) {
        Optional<CategoryDto> result = findCategoryById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List all categories", description = "Lista completa sin paginar (para selects). Para tablas usar /search.")
    @GetMapping
    public List<CategoryDto> getAll() {
        return findAllCategories.findAll();
    }

    @Operation(summary = "Search categories",
            description = "Búsqueda paginada. q busca en nombre, slug y descripción; sort = campo,asc|desc (separar varios con ;).")
    @GetMapping("/search")
    public PageResponse<CategoryDto> search(
            @Parameter(description = "Texto libre") @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        return searchCategories.search(q, PageableFactory.of(page, size, sort, SORTABLE, DEFAULT_SORT));
    }

    @Operation(summary = "Delete category", description = "Eliminación lógica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "In use", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable Long id) {
        deleteCategoryById.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bulk delete categories",
            description = "Elimina varios registros. Cada id se procesa por separado; los fallos se informan en 'failed'.")
    @PostMapping("/bulk-delete")
    public BulkOperationResult bulkDelete(@Valid @RequestBody BulkIdsRequest request) {
        return bulkDeleteCategories.deleteAll(request.ids());
    }
}
