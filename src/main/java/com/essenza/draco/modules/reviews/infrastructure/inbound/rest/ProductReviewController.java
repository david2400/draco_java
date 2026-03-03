package com.essenza.draco.modules.reviews.infrastructure.inbound.rest;

import com.essenza.draco.modules.reviews.application.input.product_review.*;
import com.essenza.draco.modules.reviews.domain.dto.product_review.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@Tag(name = "Product Reviews", description = "Operations for managing product reviews")
@Validated
public class ProductReviewController {

    private final CreateProductReviewUseCase createReviewUseCase;
    private final UpdateProductReviewUseCase updateReviewUseCase;
    private final DeleteProductReviewUseCase deleteReviewUseCase;
    private final FindProductReviewByIdUseCase findReviewByIdUseCase;
    private final FindProductReviewsUseCase findReviewsUseCase;
    private final FindProductReviewsPageUseCase findReviewsPageUseCase;
    private final SearchProductReviewsUseCase searchReviewsUseCase;
    private final VoteProductReviewHelpfulUseCase voteHelpfulUseCase;
    private final ModerateProductReviewUseCase moderateReviewUseCase;

    public ProductReviewController(CreateProductReviewUseCase createReviewUseCase,
                                   UpdateProductReviewUseCase updateReviewUseCase,
                                   DeleteProductReviewUseCase deleteReviewUseCase,
                                   FindProductReviewByIdUseCase findReviewByIdUseCase,
                                   FindProductReviewsUseCase findReviewsUseCase,
                                   FindProductReviewsPageUseCase findReviewsPageUseCase,
                                   SearchProductReviewsUseCase searchReviewsUseCase,
                                   VoteProductReviewHelpfulUseCase voteHelpfulUseCase,
                                   ModerateProductReviewUseCase moderateReviewUseCase) {
        this.createReviewUseCase = createReviewUseCase;
        this.updateReviewUseCase = updateReviewUseCase;
        this.deleteReviewUseCase = deleteReviewUseCase;
        this.findReviewByIdUseCase = findReviewByIdUseCase;
        this.findReviewsUseCase = findReviewsUseCase;
        this.findReviewsPageUseCase = findReviewsPageUseCase;
        this.searchReviewsUseCase = searchReviewsUseCase;
        this.voteHelpfulUseCase = voteHelpfulUseCase;
        this.moderateReviewUseCase = moderateReviewUseCase;
    }

    @Operation(summary = "Create review", description = "Creates a new product review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductReviewDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductReviewDto> create(@Valid @RequestBody CreateProductReviewDto input) {
        ProductReviewDto created = createReviewUseCase.create(input);
        return ResponseEntity.created(URI.create("/reviews/" + created.getId())).body(created);
    }

    @Operation(summary = "Update review", description = "Updates an existing product review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductReviewDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductReviewDto> update(
            @Parameter(description = "Review ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateProductReviewDto input) {
        ProductReviewDto updated = updateReviewUseCase.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get review by ID", description = "Retrieves a review by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductReviewDto.class))),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductReviewDto> findById(
            @Parameter(description = "Review ID", required = true) @PathVariable Long id) {
        Optional<ProductReviewDto> review = findReviewByIdUseCase.findById(id);
        return review.map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List reviews", description = "Retrieves all reviews or filters them by query parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductReviewDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<ProductReviewDto>> findAll(
            @Parameter(description = "Product ID") @RequestParam(required = false) Long productId,
            @Parameter(description = "Customer ID") @RequestParam(required = false) Long customerId,
            @Parameter(description = "Rating") @RequestParam(required = false) Integer rating,
            @Parameter(description = "Only approved reviews") @RequestParam(required = false) Boolean onlyApproved,
            @Parameter(description = "Only verified purchases") @RequestParam(required = false) Boolean onlyVerifiedPurchases,
            @Parameter(description = "Start date") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ProductReviewFiltersDto filters = ProductReviewFiltersDto.builder()
                .productId(productId)
                .customerId(customerId)
                .rating(rating)
                .onlyApproved(onlyApproved)
                .onlyVerifiedPurchases(onlyVerifiedPurchases)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        boolean hasFilters = hasFilters(filters);
        List<ProductReviewDto> reviews = hasFilters
                ? searchReviewsUseCase.search(filters)
                : findReviewsUseCase.findAll();
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Paginated reviews", description = "Retrieves reviews using pagination parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews page retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductReviewDto.class)))
    })
    @GetMapping("/page")
    public ResponseEntity<Page<ProductReviewDto>> findPage(@PageableDefault Pageable pageable) {
        Page<ProductReviewDto> page = findReviewsPageUseCase.findPage(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Delete review", description = "Deletes a review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Review ID", required = true) @PathVariable Long id) {
        boolean deleted = deleteReviewUseCase.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Vote review helpfulness", description = "Registers if a review was helpful or not")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vote registered", content = @Content),
        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
    })
    @PostMapping("/{id}/vote")
    public ResponseEntity<Void> voteHelpful(
            @Parameter(description = "Review ID", required = true) @PathVariable Long id,
            @Valid @RequestBody VoteProductReviewHelpfulDto input) {
        boolean updated = voteHelpfulUseCase.voteHelpful(id, input.getHelpful());
        return updated ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Moderate review", description = "Manually moderates a review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review moderated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductReviewDto.class))),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
    })
    @PostMapping("/{id}/moderate")
    public ResponseEntity<ProductReviewDto> moderateReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ModerateProductReviewDto input) {
        ProductReviewDto moderated = moderateReviewUseCase.moderate(id, input);
        return ResponseEntity.ok(moderated);
    }

    private boolean hasFilters(ProductReviewFiltersDto filters) {
        return filters.getProductId() != null ||
                filters.getCustomerId() != null ||
                filters.getRating() != null ||
                Boolean.TRUE.equals(filters.getOnlyApproved()) ||
                Boolean.TRUE.equals(filters.getOnlyVerifiedPurchases()) ||
                filters.getStartDate() != null ||
                filters.getEndDate() != null;
    }
}
