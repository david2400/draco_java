package com.essenza.draco.modules.recommendations.infrastructure.inbound.rest;

import com.essenza.draco.modules.recommendations.application.input.product_recommendation.CreateProductRecommendationUseCase;
import com.essenza.draco.modules.recommendations.application.input.product_recommendation.DeleteProductRecommendationUseCase;
import com.essenza.draco.modules.recommendations.application.input.product_recommendation.FindProductRecommendationByIdUseCase;
import com.essenza.draco.modules.recommendations.application.input.product_recommendation.FindProductRecommendationsUseCase;
import com.essenza.draco.modules.recommendations.application.input.product_recommendation.UpdateProductRecommendationUseCase;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.CreateProductRecommendationDto;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.ProductRecommendationDto;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.UpdateProductRecommendationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/recommendations/products")
@Tag(name = "Product Recommendations")
@Validated
public class ProductRecommendationController {

    private final CreateProductRecommendationUseCase createRecommendation;
    private final UpdateProductRecommendationUseCase updateRecommendation;
    private final DeleteProductRecommendationUseCase deleteRecommendation;
    private final FindProductRecommendationByIdUseCase findById;
    private final FindProductRecommendationsUseCase findAll;

    public ProductRecommendationController(CreateProductRecommendationUseCase createRecommendation,
                                           UpdateProductRecommendationUseCase updateRecommendation,
                                           DeleteProductRecommendationUseCase deleteRecommendation,
                                           FindProductRecommendationByIdUseCase findById,
                                           FindProductRecommendationsUseCase findAll) {
        this.createRecommendation = createRecommendation;
        this.updateRecommendation = updateRecommendation;
        this.deleteRecommendation = deleteRecommendation;
        this.findById = findById;
        this.findAll = findAll;
    }

    @Operation(summary = "Create recommendation")
    @PostMapping
    public ResponseEntity<ProductRecommendationDto> create(@Valid @RequestBody CreateProductRecommendationDto input) {
        ProductRecommendationDto created = createRecommendation.create(input);
        return ResponseEntity.created(URI.create("/recommendations/products/" + created.getId())).body(created);
    }

    @Operation(summary = "Update recommendation")
    @PutMapping("/{id}")
    public ResponseEntity<ProductRecommendationDto> update(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateProductRecommendationDto input) {
        ProductRecommendationDto updated = updateRecommendation.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get recommendation by id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductRecommendationDto> getById(@PathVariable Long id) {
        return findById.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List recommendations")
    @GetMapping
    public List<ProductRecommendationDto> list() {
        return findAll.findAll();
    }

    @Operation(summary = "Delete recommendation")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = deleteRecommendation.deleteById(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }
}
