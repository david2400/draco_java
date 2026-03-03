package com.essenza.draco.modules.product_details.application.input.product_feature;

public interface DeleteProductFeatureUseCase {
    boolean deleteById(Long productId, Long featureId);
}
