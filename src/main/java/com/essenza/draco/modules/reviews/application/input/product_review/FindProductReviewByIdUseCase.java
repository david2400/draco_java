package com.essenza.draco.modules.reviews.application.input.product_review;

import com.essenza.draco.modules.reviews.domain.dto.product_review.ProductReviewDto;

import java.util.Optional;

public interface FindProductReviewByIdUseCase {

    Optional<ProductReviewDto> findById(Long id);
}
