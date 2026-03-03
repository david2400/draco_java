// package com.essenza.draco.modules.product_details.infrastructure.inbound.graphql;

// import com.essenza.draco.modules.product_details.domain.dto.product_feature.ProductFeatureDto;
// import com.essenza.draco.modules.product_details.domain.dto.product_feature.CreateProductFeatureDto;
// import com.essenza.draco.modules.product_details.domain.dto.product_feature.UpdateProductFeatureDto;
// import com.essenza.draco.modules.product_details.application.input.product_feature.CreateProductFeatureUseCase;
// import com.essenza.draco.modules.product_details.application.input.product_feature.UpdateProductFeatureUseCase;
// import com.essenza.draco.modules.product_details.application.input.product_feature.DeleteProductFeatureUseCase;
// import com.essenza.draco.modules.product_details.application.input.product_feature.FindProductFeatureByIdUseCase;
// import com.essenza.draco.modules.product_details.application.input.product_feature.FindProductFeaturesUseCase;
// import org.springframework.graphql.data.method.annotation.Argument;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.stereotype.Controller;

// import java.util.List;

// @Controller
// public class ProductFeatureGraphQLController {

//     private final CreateProductFeatureUseCase createProductFeature;
//     private final UpdateProductFeatureUseCase updateProductFeature;
//     private final DeleteProductFeatureUseCase deleteProductFeature;
//     private final FindProductFeatureByIdUseCase findProductFeatureById;
//     private final FindProductFeaturesUseCase findProductFeatures;

//     public ProductFeatureGraphQLController(CreateProductFeatureUseCase createProductFeature,
//       UpdateProductFeatureUseCase updateProductFeature,
//       DeleteProductFeatureUseCase deleteProductFeature,
//       FindProductFeatureByIdUseCase findProductFeatureById,
//       FindProductFeaturesUseCase findProductFeatures) {
//         this.createProductFeature = createProductFeature;
//         this.updateProductFeature = updateProductFeature;
//         this.deleteProductFeature = deleteProductFeature;
//         this.findProductFeatureById = findProductFeatureById;
//         this.findProductFeatures = findProductFeatures;
//     }

//     @QueryMapping
//     public ProductFeatureDto productFeature(@Argument Long productId, @Argument Long featureId) {
//         return findProductFeatureById.findById(productId, featureId).orElse(null);
//     }

//     @QueryMapping
//     public List<ProductFeatureDto> productFeatures() {
//         return findProductFeatures.findAll();
//     }

//     @MutationMapping(name = "createProductFeature")
//     public ProductFeatureDto createProductFeature(@Argument("productFeature") CreateProductFeatureDto input) {
//         return createProductFeature.create(input);
//     }

//     @MutationMapping(name = "updateProductFeature")
//     public ProductFeatureDto updateProductFeature(@Argument Long productId, @Argument Long featureId, @Argument("productFeature") UpdateProductFeatureDto input) {
//         return updateProductFeature.update(productId, featureId, input);
//     }

//     @MutationMapping
//     public Boolean deleteProductFeature(@Argument Long productId, @Argument Long featureId) {
//         return deleteProductFeature.deleteById(productId, featureId);
//     }
// }
