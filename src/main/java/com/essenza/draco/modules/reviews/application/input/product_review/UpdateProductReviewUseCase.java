package com.essenza.draco.modules.reviews.application.input.product_review;

import com.essenza.draco.modules.reviews.application.dto.product_review.ProductReviewDto;
import com.essenza.draco.modules.reviews.application.dto.product_review.UpdateProductReviewDto;

public interface UpdateProductReviewUseCase {

    ProductReviewDto update(Long id, UpdateProductReviewDto input);
}
