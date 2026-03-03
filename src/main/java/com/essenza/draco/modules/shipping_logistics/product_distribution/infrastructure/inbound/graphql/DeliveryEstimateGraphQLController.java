// package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.delivery_estimate.CreateDeliveryEstimateUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.delivery_estimate.UpdateDeliveryEstimateUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.delivery_estimate.DeleteDeliveryEstimateUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.delivery_estimate.FindDeliveryEstimateByIdUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.delivery_estimate.FindDeliveryEstimatesUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.DeliveryEstimateDto;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.CreateDeliveryEstimateDto;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.UpdateDeliveryEstimateDto;

// import java.util.List;

// @Controller
// public class DeliveryEstimateGraphQLController {

//     private final CreateDeliveryEstimateUseCase createDeliveryEstimate;
//     private final UpdateDeliveryEstimateUseCase updateDeliveryEstimate;
//     private final DeleteDeliveryEstimateUseCase deleteDeliveryEstimate;
//     private final FindDeliveryEstimateByIdUseCase findDeliveryEstimateById;
//     private final FindDeliveryEstimatesUseCase findDeliveryEstimates;

//     public DeliveryEstimateGraphQLController(CreateDeliveryEstimateUseCase createDeliveryEstimate,
//                                            UpdateDeliveryEstimateUseCase updateDeliveryEstimate,
//                                            DeleteDeliveryEstimateUseCase deleteDeliveryEstimate,
//                                            FindDeliveryEstimateByIdUseCase findDeliveryEstimateById,
//                                            FindDeliveryEstimatesUseCase findDeliveryEstimates) {
//         this.createDeliveryEstimate = createDeliveryEstimate;
//         this.updateDeliveryEstimate = updateDeliveryEstimate;
//         this.deleteDeliveryEstimate = deleteDeliveryEstimate;
//         this.findDeliveryEstimateById = findDeliveryEstimateById;
//         this.findDeliveryEstimates = findDeliveryEstimates;
//     }

//     @MutationMapping(name = "createDeliveryEstimate")
//     public DeliveryEstimateDto createDeliveryEstimate(@Argument("deliveryEstimate") CreateDeliveryEstimateDto input) {
//         return createDeliveryEstimate.create(input);
//     }

//     @MutationMapping(name = "updateDeliveryEstimate")
//     public DeliveryEstimateDto updateDeliveryEstimate(@Argument Long id, @Argument("deliveryEstimate") UpdateDeliveryEstimateDto input) {
//         return updateDeliveryEstimate.update(id, input);
//     }

//     @MutationMapping
//     public boolean deleteDeliveryEstimate(@Argument Long id) {
//         return deleteDeliveryEstimate.deleteById(id);
//     }

//     @QueryMapping
//     public DeliveryEstimateDto deliveryEstimate(@Argument Long id) {
//         return findDeliveryEstimateById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<DeliveryEstimateDto> deliveryEstimates() {
//         return findDeliveryEstimates.findAll();
//     }
// }
