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

import com.essenza.draco.modules.catalog.application.input.brand.CreateBrandUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.UpdateBrandUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.DeleteBrandByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.FindBrandByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.FindAllBrandsUseCase;
import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.CreateBrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.UpdateBrandDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/catalog/brands")
@Tag(name = "Brands")
public class BrandController {

    private final CreateBrandUseCase createBrand;
    private final UpdateBrandUseCase updateBrand;
    private final DeleteBrandByIdUseCase deleteBrandById;
    private final FindBrandByIdUseCase findBrandById;
    private final FindAllBrandsUseCase findAllBrands;

    public BrandController(CreateBrandUseCase createBrand,
                 UpdateBrandUseCase updateBrand,
                 DeleteBrandByIdUseCase deleteBrandById,
                 FindBrandByIdUseCase findBrandById,
                 FindAllBrandsUseCase findAllBrands) {
        this.createBrand = createBrand;
        this.updateBrand = updateBrand;
        this.deleteBrandById = deleteBrandById;
        this.findBrandById = findBrandById;
        this.findAllBrands = findAllBrands;
    }

    @Operation(summary = "Create brand", description = "Creates a new brand and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Brand created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BrandDto> create(
            @RequestBody(description = "Brand to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateBrandDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateBrandDto input) {
        BrandDto created = createBrand.create(input);
        return ResponseEntity.created(URI.create("/brands/" )).body(created);
    }

    @Operation(summary = "Update brand", description = "Updates an existing brand by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandDto.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> update(
            @Parameter(description = "Brand ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Brand data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateBrandDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateBrandDto input) {
        BrandDto updated = updateBrand.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get brand by ID", description = "Returns a brand by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandDto.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getById(@Parameter(description = "Brand ID", required = true) @PathVariable Long id) {
        Optional<BrandDto> result = findBrandById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List brands", description = "Returns all brands")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of brands",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<BrandDto> getAll() {
        return findAllBrands.findAll();
    }

    @Operation(summary = "Delete brand", description = "Deletes a brand by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Brand deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Brand ID", required = true) @PathVariable Long id) {
        deleteBrandById.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
