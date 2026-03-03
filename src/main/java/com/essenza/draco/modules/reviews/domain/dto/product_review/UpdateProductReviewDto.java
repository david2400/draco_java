package com.essenza.draco.modules.reviews.domain.dto.product_review;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductReviewDto extends CreateProductReviewDto {

    @NotNull(message = "Review ID is required")
    @Positive(message = "Review ID must be positive")
    private Long id;
}
