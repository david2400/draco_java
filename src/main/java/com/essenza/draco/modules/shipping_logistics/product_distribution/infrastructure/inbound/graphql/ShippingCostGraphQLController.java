// package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.shipping_cost.CreateShippingCostUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.shipping_cost.UpdateShippingCostUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.shipping_cost.DeleteShippingCostUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.shipping_cost.FindShippingCostByIdUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.shipping_cost.FindShippingCostsUseCase;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.ShippingCostDto;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.CreateShippingCostDto;
// import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.UpdateShippingCostDto;

// import java.util.List;

// @Controller
// public class ShippingCostGraphQLController {

//     private final CreateShippingCostUseCase createShippingCost;
//     private final UpdateShippingCostUseCase updateShippingCost;
//     private final DeleteShippingCostUseCase deleteShippingCost;
//     private final FindShippingCostByIdUseCase findShippingCostById;
//     private final FindShippingCostsUseCase findShippingCosts;

//     public ShippingCostGraphQLController(CreateShippingCostUseCase createShippingCost,
//                                        UpdateShippingCostUseCase updateShippingCost,
//                                        DeleteShippingCostUseCase deleteShippingCost,
//                                        FindShippingCostByIdUseCase findShippingCostById,
//                                        FindShippingCostsUseCase findShippingCosts) {
//         this.createShippingCost = createShippingCost;
//         this.updateShippingCost = updateShippingCost;
//         this.deleteShippingCost = deleteShippingCost;
//         this.findShippingCostById = findShippingCostById;
//         this.findShippingCosts = findShippingCosts;
//     }

//     @MutationMapping(name = "createShippingCost")
//     public ShippingCostDto createShippingCost(@Argument("shippingCost") CreateShippingCostDto input) {
//         return createShippingCost.create(input);
//     }

//     @MutationMapping(name = "updateShippingCost")
//     public ShippingCostDto updateShippingCost(@Argument Long id, @Argument("shippingCost") UpdateShippingCostDto input) {
//         return updateShippingCost.update(id, input);
//     }

//     @MutationMapping
//     public boolean deleteShippingCost(@Argument Long id) {
//         return deleteShippingCost.deleteById(id);
//     }

//     @QueryMapping
//     public ShippingCostDto shippingCost(@Argument Long id) {
//         return findShippingCostById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<ShippingCostDto> shippingCosts() {
//         return findShippingCosts.findAll();
//     }
// }
