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

import com.essenza.draco.modules.inventory.application.input.product_combo.CreateProductComboUseCase;
import com.essenza.draco.modules.inventory.application.input.product_combo.UpdateProductComboUseCase;
import com.essenza.draco.modules.inventory.application.input.product_combo.DeleteProductComboUseCase;
import com.essenza.draco.modules.inventory.application.input.product_combo.FindProductComboByIdUseCase;
import com.essenza.draco.modules.inventory.application.input.product_combo.FindProductCombosUseCase;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.ProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.CreateProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.UpdateProductComboDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product-combos")
public class ProductComboController {

    private final CreateProductComboUseCase createProductCombo;
    private final UpdateProductComboUseCase updateProductCombo;
    private final DeleteProductComboUseCase deleteProductCombo;
    private final FindProductComboByIdUseCase findProductComboById;
    private final FindProductCombosUseCase findProductCombos;

    public ProductComboController(CreateProductComboUseCase createProductCombo,
                                  UpdateProductComboUseCase updateProductCombo,
                                  DeleteProductComboUseCase deleteProductCombo,
                                  FindProductComboByIdUseCase findProductComboById,
                                  FindProductCombosUseCase findProductCombos) {
        this.createProductCombo = createProductCombo;
        this.updateProductCombo = updateProductCombo;
        this.deleteProductCombo = deleteProductCombo;
        this.findProductComboById = findProductComboById;
        this.findProductCombos = findProductCombos;
    }

    @Operation(summary = "Create product combo", description = "Creates a new product combo and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product combo created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductComboDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductComboDto> create(
            @RequestBody(description = "Product combo to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateProductComboDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateProductComboDto input) {
        ProductComboDto created = createProductCombo.create(input);
        return ResponseEntity.created(URI.create("/product-combos/" )).body(created);
    }

    @Operation(summary = "Update product combo", description = "Updates an existing product combo by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product combo updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductComboDto.class))),
            @ApiResponse(responseCode = "404", description = "Product combo not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductComboDto> update(
            @Parameter(description = "Product combo ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Product combo data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateProductComboDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateProductComboDto input) {
        ProductComboDto updated = updateProductCombo.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get product combo by ID", description = "Returns a product combo by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product combo found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductComboDto.class))),
            @ApiResponse(responseCode = "404", description = "Product combo not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductComboDto> getById(@Parameter(description = "Product combo ID", required = true) @PathVariable Long id) {
        Optional<ProductComboDto> result = findProductComboById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List product combos", description = "Returns all product combos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of product combos",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<ProductComboDto> getAll() {
        return findProductCombos.findAll();
    }

    @Operation(summary = "Delete product combo", description = "Deletes a product combo by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product combo deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product combo not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Product combo ID", required = true) @PathVariable Long id) {
        Boolean deleted = deleteProductCombo.deleteById(id);
        return Boolean.TRUE.equals(deleted) ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
