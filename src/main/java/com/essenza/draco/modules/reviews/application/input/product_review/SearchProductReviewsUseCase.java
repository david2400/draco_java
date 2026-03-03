package com.essenza.draco.modules.reviews.application.input.product_review;

import com.essenza.draco.modules.reviews.domain.dto.product_review.ProductReviewDto;
import com.essenza.draco.modules.reviews.domain.dto.product_review.ProductReviewFiltersDto;

import java.util.List;

public interface SearchProductReviewsUseCase {

    List<ProductReviewDto> search(ProductReviewFiltersDto filters);
}
