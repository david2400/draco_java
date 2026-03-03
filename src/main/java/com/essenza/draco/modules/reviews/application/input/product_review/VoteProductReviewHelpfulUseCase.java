package com.essenza.draco.modules.reviews.application.input.product_review;

public interface VoteProductReviewHelpfulUseCase {

    boolean voteHelpful(Long id, boolean helpful);
}
