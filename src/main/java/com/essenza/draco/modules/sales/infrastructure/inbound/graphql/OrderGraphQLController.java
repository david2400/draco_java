//package com.essenza.draco.modules.sales.infrastructure.inbound.graphql;
//
//import com.essenza.draco.modules.sales.domain.dto.order.CreateOrderDto;
//import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;
//import com.essenza.draco.modules.sales.domain.dto.order.UpdateOrderDto;
//import com.essenza.draco.modules.sales.application.input.order.CreateOrderUseCase;
//import com.essenza.draco.modules.sales.application.input.order.UpdateOrderUseCase;
//import com.essenza.draco.modules.sales.application.input.order.DeleteOrderUseCase;
//import com.essenza.draco.modules.sales.application.input.order.FindOrderByIdUseCase;
//import com.essenza.draco.modules.sales.application.input.order.FindOrdersUseCase;
//import org.springframework.graphql.data.method.annotation.Argument;
//import org.springframework.graphql.data.method.annotation.MutationMapping;
//import org.springframework.graphql.data.method.annotation.QueryMapping;
//import org.springframework.stereotype.Controller;
//
//import java.util.List;
//
//@Controller
//public class OrderGraphQLController {
//
//    private final CreateOrderUseCase createOrder;
//    private final UpdateOrderUseCase updateOrder;
//    private final DeleteOrderUseCase deleteOrder;
//    private final FindOrderByIdUseCase findOrderById;
//    private final FindOrdersUseCase findOrders;
//
//    public OrderGraphQLController(CreateOrderUseCase createOrder,
//         UpdateOrderUseCase updateOrder,
//         DeleteOrderUseCase deleteOrder,
//         FindOrderByIdUseCase findOrderById,
//         FindOrdersUseCase findOrders) {
//        this.createOrder = createOrder;
//        this.updateOrder = updateOrder;
//        this.deleteOrder = deleteOrder;
//        this.findOrderById = findOrderById;
//        this.findOrders = findOrders;
//    }
//
//    @MutationMapping(name = "createOrder")
//    public OrderDto createOrder(@Argument("order") CreateOrderDto input) {
//        return createOrder.create(input);
//    }
//
//    @MutationMapping(name = "updateOrder")
//    public OrderDto updateOrder(@Argument Long id, @Argument("order") UpdateOrderDto input) {
//        return updateOrder.update(id, input);
//    }
//
//    @MutationMapping
//    public boolean deleteOrder(@Argument Long id) {
//        return deleteOrder.deleteById(id);
//    }
//
//    @QueryMapping
//    public OrderDto order(@Argument Long id) {
//        return findOrderById.findById(id).orElse(null);
//    }
//
//    @QueryMapping
//    public List<OrderDto> orders() {
//        return findOrders.findAll();
//    }
//}
