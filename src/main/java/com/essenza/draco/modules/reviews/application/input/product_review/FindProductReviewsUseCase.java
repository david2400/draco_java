package com.essenza.draco.modules.reviews.application.input.product_review;

import com.essenza.draco.modules.reviews.domain.dto.product_review.ProductReviewDto;

import java.util.List;

public interface FindProductReviewsUseCase {

    List<ProductReviewDto> findAll();
}
