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
import com.essenza.draco.modules.catalog.application.input.subcategory.BulkDeleteSubcategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.CreateSubcategoryUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.DeleteSubcategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.FindAllSubcategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.FindSubcategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.SearchSubcategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.UpdateSubcategoryUseCase;
import com.essenza.draco.modules.catalog.application.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.application.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.application.dto.subcategory.UpdateSubcategoryDto;

@RestController
@RequestMapping("/catalog/subcategories")
@Tag(name = "Subcategories")
public class SubcategoryController {

    /** Campos por los que se permite ordenar (lista blanca). */
    private static final Set<String> SORTABLE = Set.of("id", "name", "slug", "createdAt", "updatedAt", "categoryId");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("name").ignoreCase());

    private final CreateSubcategoryUseCase createSubcategory;
    private final UpdateSubcategoryUseCase updateSubcategory;
    private final DeleteSubcategoryByIdUseCase deleteSubcategoryById;
    private final FindSubcategoryByIdUseCase findSubcategoryById;
    private final FindAllSubcategoriesUseCase findAllSubcategories;
    private final SearchSubcategoriesUseCase searchSubcategories;
    private final BulkDeleteSubcategoriesUseCase bulkDeleteSubcategories;
    private final Validator validator;

    public SubcategoryController(CreateSubcategoryUseCase createSubcategory,
                         UpdateSubcategoryUseCase updateSubcategory,
                         DeleteSubcategoryByIdUseCase deleteSubcategoryById,
                         FindSubcategoryByIdUseCase findSubcategoryById,
                         FindAllSubcategoriesUseCase findAllSubcategories,
                         SearchSubcategoriesUseCase searchSubcategories,
                         BulkDeleteSubcategoriesUseCase bulkDeleteSubcategories,
                         Validator validator) {
        this.createSubcategory = createSubcategory;
        this.updateSubcategory = updateSubcategory;
        this.deleteSubcategoryById = deleteSubcategoryById;
        this.findSubcategoryById = findSubcategoryById;
        this.findAllSubcategories = findAllSubcategories;
        this.searchSubcategories = searchSubcategories;
        this.bulkDeleteSubcategories = bulkDeleteSubcategories;
        this.validator = validator;
    }

    @Operation(summary = "Create subcategory", description = "Crea el registro. El slug es opcional (se genera desde el nombre).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubcategoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Duplicated name or slug", content = @Content)
    })
    @PostMapping
    public ResponseEntity<SubcategoryDto> create(@Valid @RequestBody CreateSubcategoryDto input) {
        SubcategoryDto created = createSubcategory.create(input);
        return ResponseEntity.created(URI.create("/catalog/subcategories/" + created.getId())).body(created);
    }

    @Operation(summary = "Update subcategory", description = "Actualiza el registro. El id se toma de la URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubcategoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Duplicated name or slug", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<SubcategoryDto> update(@Parameter(description = "ID", required = true) @PathVariable Long id,
                                         @RequestBody UpdateSubcategoryDto input) {
        // El id del cuerpo es opcional: se fija desde la URL y luego se valida.
        input.setId(id);
        Set<ConstraintViolation<UpdateSubcategoryDto>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return ResponseEntity.ok(updateSubcategory.update(id, input));
    }

    @Operation(summary = "Get subcategory by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SubcategoryDto> getById(@Parameter(description = "ID", required = true) @PathVariable Long id) {
        Optional<SubcategoryDto> result = findSubcategoryById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List all subcategories", description = "Lista completa sin paginar (para selects). Para tablas usar /search.")
    @GetMapping
    public List<SubcategoryDto> getAll() {
        return findAllSubcategories.findAll();
    }

    @Operation(summary = "Search subcategories",
            description = "Búsqueda paginada. q busca en nombre, slug y descripción; sort = campo,asc|desc (separar varios con ;).")
    @GetMapping("/search")
    public PageResponse<SubcategoryDto> search(
            @Parameter(description = "Texto libre") @RequestParam(value = "q", required = false) String q,
            @Parameter(description = "Filtrar por categoría") @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        return searchSubcategories.search(q, categoryId, PageableFactory.of(page, size, sort, SORTABLE, DEFAULT_SORT));
    }

    @Operation(summary = "Delete subcategory", description = "Eliminación lógica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "In use", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable Long id) {
        deleteSubcategoryById.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bulk delete subcategories",
            description = "Elimina varios registros. Cada id se procesa por separado; los fallos se informan en 'failed'.")
    @PostMapping("/bulk-delete")
    public BulkOperationResult bulkDelete(@Valid @RequestBody BulkIdsRequest request) {
        return bulkDeleteSubcategories.deleteAll(request.ids());
    }
}
