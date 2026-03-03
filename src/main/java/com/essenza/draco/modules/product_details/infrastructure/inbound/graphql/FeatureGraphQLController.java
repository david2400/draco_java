// package com.essenza.draco.modules.product_details.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.product_details.application.input.feature.CreateFeatureUseCase;
// import com.essenza.draco.modules.product_details.application.input.feature.UpdateFeatureUseCase;
// import com.essenza.draco.modules.product_details.application.input.feature.DeleteFeatureUseCase;
// import com.essenza.draco.modules.product_details.application.input.feature.FindFeatureByIdUseCase;
// import com.essenza.draco.modules.product_details.application.input.feature.FindFeaturesUseCase;
// import com.essenza.draco.modules.product_details.domain.dto.feature.FeatureDto;
// import com.essenza.draco.modules.product_details.domain.dto.feature.CreateFeatureDto;
// import com.essenza.draco.modules.product_details.domain.dto.feature.UpdateFeatureDto;

// import java.util.List;

// @Controller
// public class FeatureGraphQLController {

//     private final CreateFeatureUseCase createFeature;
//     private final UpdateFeatureUseCase updateFeature;
//     private final DeleteFeatureUseCase deleteFeature;
//     private final FindFeatureByIdUseCase findFeatureById;
//     private final FindFeaturesUseCase findFeatures;

//     public FeatureGraphQLController(CreateFeatureUseCase createFeature,
//        UpdateFeatureUseCase updateFeature,
//        DeleteFeatureUseCase deleteFeature,
//        FindFeatureByIdUseCase findFeatureById,
//        FindFeaturesUseCase findFeatures) {
//         this.createFeature = createFeature;
//         this.updateFeature = updateFeature;
//         this.deleteFeature = deleteFeature;
//         this.findFeatureById = findFeatureById;
//         this.findFeatures = findFeatures;
//     }

//     @QueryMapping
//     public FeatureDto feature(@Argument Long id) {
//         return findFeatureById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<FeatureDto> features() {
//         return findFeatures.findAll();
//     }

//     @MutationMapping(name = "createFeature")
//     public FeatureDto createFeature(@Argument("feature") CreateFeatureDto input) {
//         return createFeature.create(input);
//     }

//     @MutationMapping(name = "updateFeature")
//     public FeatureDto updateFeature(@Argument Long id, @Argument("feature") UpdateFeatureDto input) {
//         return updateFeature.update(id, input);
//     }

//     @MutationMapping
//     public Boolean deleteFeature(@Argument Long id) {
//         return deleteFeature.deleteById(id);
//     }
// }
