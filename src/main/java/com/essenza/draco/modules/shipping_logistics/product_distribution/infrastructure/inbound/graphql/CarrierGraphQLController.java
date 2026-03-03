// package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.CreateCarrierUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.UpdateCarrierUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.DeleteCarrierUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.FindCarrierByIdUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.FindCarriersUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CarrierDto;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CreateCarrierDto;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.UpdateCarrierDto;

// import java.util.List;

// @Controller
// public class CarrierGraphQLController {

//     private final CreateCarrierUseCase createCarrier;
//     private final UpdateCarrierUseCase updateCarrier;
//     private final DeleteCarrierUseCase deleteCarrier;
//     private final FindCarrierByIdUseCase findCarrierById;
//     private final FindCarriersUseCase findCarriers;

//     public CarrierGraphQLController(CreateCarrierUseCase createCarrier,
//                                   UpdateCarrierUseCase updateCarrier,
//                                   DeleteCarrierUseCase deleteCarrier,
//                                   FindCarrierByIdUseCase findCarrierById,
//                                   FindCarriersUseCase findCarriers) {
//         this.createCarrier = createCarrier;
//         this.updateCarrier = updateCarrier;
//         this.deleteCarrier = deleteCarrier;
//         this.findCarrierById = findCarrierById;
//         this.findCarriers = findCarriers;
//     }

//     @MutationMapping(name = "createCarrier")
//     public CarrierDto createCarrier(@Argument("carrier") CreateCarrierDto input) {
//         return createCarrier.create(input);
//     }

//     @MutationMapping(name = "updateCarrier")
//     public CarrierDto updateCarrier(@Argument Long id, @Argument("carrier") UpdateCarrierDto input) {
//         return updateCarrier.update(id, input);
//     }

//     @MutationMapping
//     public boolean deleteCarrier(@Argument Long id) {
//         return deleteCarrier.deleteById(id);
//     }

//     @QueryMapping
//     public CarrierDto carrier(@Argument Long id) {
//         return findCarrierById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<CarrierDto> carriers() {
//         return findCarriers.findAll();
//     }
// }
