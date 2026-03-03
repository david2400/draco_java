// package com.essenza.draco.modules.devolution.infrastructure.inbound.graphql;

// import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.CreateOrderDevolutionDetailDto;
// import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.OrderDevolutionDetailDto;
// import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.UpdateOrderDevolutionDetailDto;
// import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.CreateOrderDevolutionDetailUseCase;
// import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.UpdateOrderDevolutionDetailUseCase;
// import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.DeleteOrderDevolutionDetailUseCase;
// import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.FindOrderDevolutionDetailByIdUseCase;
// import com.essenza.draco.modules.devolution.application.input.order_devolution_detail.FindOrderDevolutionDetailsUseCase;
// import org.springframework.graphql.data.method.annotation.Argument;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.stereotype.Controller;

// import java.util.List;

// @Controller
// public class OrderDevolutionDetailGraphQLController {

//     private final CreateOrderDevolutionDetailUseCase createOrderDevolutionDetail;
//     private final UpdateOrderDevolutionDetailUseCase updateOrderDevolutionDetail;
//     private final DeleteOrderDevolutionDetailUseCase deleteOrderDevolutionDetail;
//     private final FindOrderDevolutionDetailByIdUseCase findOrderDevolutionDetailById;
//     private final FindOrderDevolutionDetailsUseCase findOrderDevolutionDetails;

//     public OrderDevolutionDetailGraphQLController(CreateOrderDevolutionDetailUseCase createOrderDevolutionDetail,
//                                                  UpdateOrderDevolutionDetailUseCase updateOrderDevolutionDetail,
//                                                  DeleteOrderDevolutionDetailUseCase deleteOrderDevolutionDetail,
//                                                  FindOrderDevolutionDetailByIdUseCase findOrderDevolutionDetailById,
//                                                  FindOrderDevolutionDetailsUseCase findOrderDevolutionDetails) {
//         this.createOrderDevolutionDetail = createOrderDevolutionDetail;
//         this.updateOrderDevolutionDetail = updateOrderDevolutionDetail;
//         this.deleteOrderDevolutionDetail = deleteOrderDevolutionDetail;
//         this.findOrderDevolutionDetailById = findOrderDevolutionDetailById;
//         this.findOrderDevolutionDetails = findOrderDevolutionDetails;
//     }

//     @MutationMapping(name = "createOrderDevolutionDetail")
//     public OrderDevolutionDetailDto createOrderDevolutionDetail(@Argument("orderDevolutionDetail") CreateOrderDevolutionDetailDto input) {
//         return createOrderDevolutionDetail.create(input);
//     }

//     @MutationMapping(name = "updateOrderDevolutionDetail")
//     public OrderDevolutionDetailDto updateOrderDevolutionDetail(@Argument Long id, @Argument("orderDevolutionDetail") UpdateOrderDevolutionDetailDto input) {
//         return updateOrderDevolutionDetail.update(id, input);
//     }

//     @MutationMapping
//     public boolean deleteOrderDevolutionDetail(@Argument Long id) {
//         return deleteOrderDevolutionDetail.deleteById(id);
//     }

//     @QueryMapping
//     public OrderDevolutionDetailDto orderDevolutionDetail(@Argument Long id) {
//         return findOrderDevolutionDetailById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<OrderDevolutionDetailDto> orderDevolutionDetails() {
//         return findOrderDevolutionDetails.findAll();
//     }
// }
