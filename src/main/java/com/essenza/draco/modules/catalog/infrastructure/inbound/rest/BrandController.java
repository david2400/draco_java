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
import com.essenza.draco.modules.catalog.application.input.brand.BulkDeleteBrandsUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.CreateBrandUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.DeleteBrandByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.FindAllBrandsUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.FindBrandByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.SearchBrandsUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.UpdateBrandUseCase;
import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.CreateBrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.UpdateBrandDto;

@RestController
@RequestMapping("/catalog/brands")
@Tag(name = "Brands")
public class BrandController {

    /** Campos por los que se permite ordenar (lista blanca). */
    private static final Set<String> SORTABLE = Set.of("id", "name", "slug", "createdAt", "updatedAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("name").ignoreCase());

    private final CreateBrandUseCase createBrand;
    private final UpdateBrandUseCase updateBrand;
    private final DeleteBrandByIdUseCase deleteBrandById;
    private final FindBrandByIdUseCase findBrandById;
    private final FindAllBrandsUseCase findAllBrands;
    private final SearchBrandsUseCase searchBrands;
    private final BulkDeleteBrandsUseCase bulkDeleteBrands;
    private final Validator validator;

    public BrandController(CreateBrandUseCase createBrand,
                         UpdateBrandUseCase updateBrand,
                         DeleteBrandByIdUseCase deleteBrandById,
                         FindBrandByIdUseCase findBrandById,
                         FindAllBrandsUseCase findAllBrands,
                         SearchBrandsUseCase searchBrands,
                         BulkDeleteBrandsUseCase bulkDeleteBrands,
                         Validator validator) {
        this.createBrand = createBrand;
        this.updateBrand = updateBrand;
        this.deleteBrandById = deleteBrandById;
        this.findBrandById = findBrandById;
        this.findAllBrands = findAllBrands;
        this.searchBrands = searchBrands;
        this.bulkDeleteBrands = bulkDeleteBrands;
        this.validator = validator;
    }

    @Operation(summary = "Create brand", description = "Crea el registro. El slug es opcional (se genera desde el nombre).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Duplicated name or slug", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BrandDto> create(@Valid @RequestBody CreateBrandDto input) {
        BrandDto created = createBrand.create(input);
        return ResponseEntity.created(URI.create("/catalog/brands/" + created.getId())).body(created);
    }

    @Operation(summary = "Update brand", description = "Actualiza el registro. El id se toma de la URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Duplicated name or slug", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> update(@Parameter(description = "ID", required = true) @PathVariable Long id,
                                         @RequestBody UpdateBrandDto input) {
        // El id del cuerpo es opcional: se fija desde la URL y luego se valida.
        input.setId(id);
        Set<ConstraintViolation<UpdateBrandDto>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return ResponseEntity.ok(updateBrand.update(id, input));
    }

    @Operation(summary = "Get brand by ID")
    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getById(@Parameter(description = "ID", required = true) @PathVariable Long id) {
        Optional<BrandDto> result = findBrandById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List all brands", description = "Lista completa sin paginar (para selects). Para tablas usar /search.")
    @GetMapping
    public List<BrandDto> getAll() {
        return findAllBrands.findAll();
    }

    @Operation(summary = "Search brands",
            description = "Búsqueda paginada. q busca en nombre, slug y descripción; sort = campo,asc|desc (separar varios con ;).")
    @GetMapping("/search")
    public PageResponse<BrandDto> search(
            @Parameter(description = "Texto libre") @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        return searchBrands.search(q, PageableFactory.of(page, size, sort, SORTABLE, DEFAULT_SORT));
    }

    @Operation(summary = "Delete brand", description = "Eliminación lógica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "In use", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable Long id) {
        deleteBrandById.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bulk delete brands",
            description = "Elimina varios registros. Cada id se procesa por separado; los fallos se informan en 'failed'.")
    @PostMapping("/bulk-delete")
    public BulkOperationResult bulkDelete(@Valid @RequestBody BulkIdsRequest request) {
        return bulkDeleteBrands.deleteAll(request.ids());
    }
}
