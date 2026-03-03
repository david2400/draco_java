package com.essenza.draco.modules.inventory.infrastructure.inbound.rest;

import com.essenza.draco.modules.inventory.application.input.product.*;
import com.essenza.draco.modules.inventory.domain.dto.product.CreateProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductFilter;
import com.essenza.draco.modules.inventory.domain.dto.product.UpdateProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@Tag(name = "Products")
public class ProductController {

    private final CreateProductUseCase createProduct;
    private final UpdateProductUseCase updateProduct;
    private final DeleteProductUseCase deleteProduct;
    private final FindProductByIdUseCase findProductById;
    private final FindProductsUseCase findProducts;
    private final FindProductsPageUseCase findProductsPage;

    public ProductController(CreateProductUseCase createProduct,
                             UpdateProductUseCase updateProduct,
                             DeleteProductUseCase deleteProduct,
                             FindProductByIdUseCase findProductById,
                             FindProductsUseCase findProducts,
                             FindProductsPageUseCase findProductsPage) {
        this.createProduct = createProduct;
        this.updateProduct = updateProduct;
        this.deleteProduct = deleteProduct;
        this.findProductById = findProductById;
        this.findProducts = findProducts;
        this.findProductsPage = findProductsPage;
    }

    @Operation(summary = "Create product", description = "Creates a new product and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductDto> create(
            @RequestBody(description = "Product to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateProductDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateProductDto input) {
        ProductDto created = createProduct.create(input);
        return ResponseEntity.created(URI.create("/products/")).body(created);
    }

    @Operation(summary = "Update product", description = "Updates an existing product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Product data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateProductDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateProductDto input) {
        ProductDto updated = updateProduct.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete product", description = "Deletes a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Product ID", required = true) @PathVariable Long id) {
        boolean deleted = deleteProduct.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Get product by ID", description = "Returns a product by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@Parameter(description = "Product ID", required = true) @PathVariable Long id) {
        Optional<ProductDto> result = findProductById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List products", description = "Returns products paginated; use page, size and sort=field,asc;other,desc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public Page<ProductDto> getAll(@RequestParam(value = "page", required = false) Integer page,
                                   @RequestParam(value = "size", required = false) Integer size,
                                   @RequestParam(value = "sort", required = false) String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        return findProducts.findAll(pageable);
    }

    @Operation(summary = "Search products", description = "Search products with a filter body and pagination params page, size, sort")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/search")
    public Page<ProductDto> search(@RequestParam(value = "page", required = false) Integer page,
                                   @RequestParam(value = "size", required = false) Integer size,
                                   @RequestParam(value = "sort", required = false) String sort,
                                   @RequestBody(description = "Product filter", required = true,
                                           content = @Content(schema = @Schema(implementation = ProductFilter.class)))
                                   @org.springframework.web.bind.annotation.RequestBody ProductFilter filter) {
        Pageable pageable = buildPageable(page, size, sort);
        return findProductsPage.findAllPage(pageable, filter);
    }

    private Pageable buildPageable(Integer page, Integer size, String sortParam) {
        int p = page != null ? Math.max(page, 0) : 0;
        int s = size != null ? Math.max(size, 1) : 20;
        Sort sort = parseSort(sortParam);
        return PageRequest.of(p, s, sort);
    }

    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) return Sort.unsorted();
        String[] parts = sortParam.split(";");
        Sort combined = Sort.unsorted();
        for (String p : parts) {
            String[] seg = p.trim().split(",");
            if (seg.length == 0 || seg[0].isBlank()) continue;
            String prop = seg[0].trim();
            Sort.Direction dir = (seg.length > 1 && "desc".equalsIgnoreCase(seg[1].trim()))
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort next = Sort.by(new Sort.Order(dir, prop));
            combined = combined.isUnsorted() ? next : combined.and(next);
        }
        return combined;
    }
}
