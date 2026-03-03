package com.essenza.draco.modules.reviews.domain.dto.product_review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewFiltersDto {

    private Long productId;

    private Long customerId;

    private Integer rating;

    private Boolean onlyApproved;

    private Boolean onlyVerifiedPurchases;

    private LocalDate startDate;

    private LocalDate endDate;
}
