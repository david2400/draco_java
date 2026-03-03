// package com.essenza.draco.modules.devolution.infrastructure.inbound.graphql;

// import com.essenza.draco.modules.devolution.domain.dto.order_devolution.CreateOrderDevolutionDto;
// import com.essenza.draco.modules.devolution.domain.dto.order_devolution.OrderDevolutionDto;
// import com.essenza.draco.modules.devolution.domain.dto.order_devolution.UpdateOrderDevolutionDto;
// import com.essenza.draco.modules.devolution.application.input.order_devolution.CreateOrderDevolutionUseCase;
// import com.essenza.draco.modules.devolution.application.input.order_devolution.UpdateOrderDevolutionUseCase;
// import com.essenza.draco.modules.devolution.application.input.order_devolution.DeleteOrderDevolutionUseCase;
// import com.essenza.draco.modules.devolution.application.input.order_devolution.FindOrderDevolutionByIdUseCase;
// import com.essenza.draco.modules.devolution.application.input.order_devolution.FindOrderDevolutionsUseCase;
// import org.springframework.graphql.data.method.annotation.Argument;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.stereotype.Controller;

// import java.util.List;

// @Controller
// public class OrderDevolutionGraphQLController {

//     private final CreateOrderDevolutionUseCase createOrderDevolution;
//     private final UpdateOrderDevolutionUseCase updateOrderDevolution;
//     private final DeleteOrderDevolutionUseCase deleteOrderDevolution;
//     private final FindOrderDevolutionByIdUseCase findOrderDevolutionById;
//     private final FindOrderDevolutionsUseCase findOrderDevolutions;

//     public OrderDevolutionGraphQLController(CreateOrderDevolutionUseCase createOrderDevolution,
//                                            UpdateOrderDevolutionUseCase updateOrderDevolution,
//                                            DeleteOrderDevolutionUseCase deleteOrderDevolution,
//                                            FindOrderDevolutionByIdUseCase findOrderDevolutionById,
//                                            FindOrderDevolutionsUseCase findOrderDevolutions) {
//         this.createOrderDevolution = createOrderDevolution;
//         this.updateOrderDevolution = updateOrderDevolution;
//         this.deleteOrderDevolution = deleteOrderDevolution;
//         this.findOrderDevolutionById = findOrderDevolutionById;
//         this.findOrderDevolutions = findOrderDevolutions;
//     }

//     @MutationMapping(name = "createOrderDevolution")
//     public OrderDevolutionDto createOrderDevolution(@Argument("orderDevolution") CreateOrderDevolutionDto input) {
//         return createOrderDevolution.create(input);
//     }

//     @MutationMapping(name = "updateOrderDevolution")
//     public OrderDevolutionDto updateOrderDevolution(@Argument Long id, @Argument("orderDevolution") UpdateOrderDevolutionDto input) {
//         return updateOrderDevolution.update(id, input);
//     }

//     @MutationMapping
//     public boolean deleteOrderDevolution(@Argument Long id) {
//         return deleteOrderDevolution.deleteById(id);
//     }

//     @QueryMapping
//     public OrderDevolutionDto orderDevolution(@Argument Long id) {
//         return findOrderDevolutionById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<OrderDevolutionDto> orderDevolutions() {
//         return findOrderDevolutions.findAll();
//     }
// }
