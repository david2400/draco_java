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

import com.essenza.draco.modules.inventory.application.input.product_child.CreateProductChildUseCase;
import com.essenza.draco.modules.inventory.application.input.product_child.UpdateProductChildUseCase;
import com.essenza.draco.modules.inventory.application.input.product_child.DeleteProductChildUseCase;
import com.essenza.draco.modules.inventory.application.input.product_child.FindProductChildByIdUseCase;
import com.essenza.draco.modules.inventory.application.input.product_child.FindProductChildrenUseCase;
import com.essenza.draco.modules.inventory.domain.dto.product_child.ProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.CreateProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.UpdateProductChildDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inventory/product_children")
public class ProductChildController {

    private final CreateProductChildUseCase createProductChild;
    private final UpdateProductChildUseCase updateProductChild;
    private final DeleteProductChildUseCase deleteProductChild;
    private final FindProductChildByIdUseCase findProductChildById;
    private final FindProductChildrenUseCase findProductChildren;

    public ProductChildController(CreateProductChildUseCase createProductChild,
                                  UpdateProductChildUseCase updateProductChild,
                                  DeleteProductChildUseCase deleteProductChild,
                                  FindProductChildByIdUseCase findProductChildById,
                                  FindProductChildrenUseCase findProductChildren) {
        this.createProductChild = createProductChild;
        this.updateProductChild = updateProductChild;
        this.deleteProductChild = deleteProductChild;
        this.findProductChildById = findProductChildById;
        this.findProductChildren = findProductChildren;
    }

    @Operation(summary = "Create product child", description = "Creates a new product child and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product child created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductChildDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductChildDto> create(
            @RequestBody(description = "Product child to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateProductChildDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateProductChildDto input) {
        ProductChildDto created = createProductChild.create(input);
        return ResponseEntity.created(URI.create("/product-children/" )).body(created);
    }

    @Operation(summary = "Update product child", description = "Updates an existing product child by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product child updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductChildDto.class))),
            @ApiResponse(responseCode = "404", description = "Product child not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductChildDto> update(
            @Parameter(description = "Product child ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Product child data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateProductChildDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateProductChildDto input) {
        ProductChildDto updated = updateProductChild.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get product child by ID", description = "Returns a product child by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product child found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductChildDto.class))),
            @ApiResponse(responseCode = "404", description = "Product child not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductChildDto> getById(@Parameter(description = "Product child ID", required = true) @PathVariable Long id) {
        Optional<ProductChildDto> result = findProductChildById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List product children", description = "Returns all product children")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of product children",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<ProductChildDto> getAll() {
        return findProductChildren.findAll();
    }

    @Operation(summary = "Delete product child", description = "Deletes a product child by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product child deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product child not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Product child ID", required = true) @PathVariable Long id) {
        Boolean deleted = deleteProductChild.deleteById(id);
        return Boolean.TRUE.equals(deleted) ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
