package com.essenza.draco.modules.reviews.domain.dto.product_review;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteProductReviewHelpfulDto {

    @NotNull(message = "Helpful decision is required")
    private Boolean helpful;
}
