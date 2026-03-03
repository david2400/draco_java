package com.essenza.draco.modules.reviews.infrastructure.outbound.repositories.product_review;

import com.essenza.draco.modules.reviews.domain.dto.product_review.ProductReviewFiltersDto;
import com.essenza.draco.modules.reviews.infrastructure.outbound.persistence.mysql.shop.ProductReviewEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class ProductReviewSpecification {

    private ProductReviewSpecification() {
    }

    public static Specification<ProductReviewEntity> withFilters(ProductReviewFiltersDto filters) {
        Specification<ProductReviewEntity> spec = Specification.where((Specification<ProductReviewEntity>) null);

        if (filters == null) {
            return spec;
        }

        if (filters.getProductId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("productId"), filters.getProductId()));
        }

        if (filters.getCustomerId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("customerId"), filters.getCustomerId()));
        }

        if (filters.getRating() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("rating"), filters.getRating()));
        }

        if (Boolean.TRUE.equals(filters.getOnlyApproved())) {
            spec = spec.and((root, query, cb) -> cb.isTrue(root.get("isApproved")));
        }

        if (Boolean.TRUE.equals(filters.getOnlyVerifiedPurchases())) {
            spec = spec.and((root, query, cb) -> cb.isTrue(root.get("isVerifiedPurchase")));
        }

        if (filters.getStartDate() != null) {
            LocalDateTime startDateTime = startOfDay(filters.getStartDate());
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("reviewDate"), startDateTime));
        }

        if (filters.getEndDate() != null) {
            LocalDateTime endDateTime = endOfDay(filters.getEndDate());
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("reviewDate"), endDateTime));
        }

        return spec;
    }

    private static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    private static LocalDateTime endOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
    }
}
