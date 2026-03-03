package com.essenza.draco.modules.reviews.application.input.product_review;

import com.essenza.draco.modules.reviews.domain.dto.product_review.ProductReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindProductReviewsPageUseCase {

    Page<ProductReviewDto> findPage(Pageable pageable);
}
