package com.essenza.draco.modules.reviews.application.input.product_review;

import com.essenza.draco.modules.reviews.domain.dto.product_review.ModerateProductReviewDto;
import com.essenza.draco.modules.reviews.domain.dto.product_review.ProductReviewDto;

public interface ModerateProductReviewUseCase {

    ProductReviewDto moderate(Long id, ModerateProductReviewDto input);
}
