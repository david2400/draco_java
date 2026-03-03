package com.essenza.draco.modules.reviews.application.input.product_review;

import com.essenza.draco.modules.reviews.domain.dto.product_review.CreateProductReviewDto;
import com.essenza.draco.modules.reviews.domain.dto.product_review.ProductReviewDto;

public interface CreateProductReviewUseCase {

    ProductReviewDto create(CreateProductReviewDto input);
}
