//package com.essenza.draco.modules.sales.infrastructure.inbound.graphql;
//
//import com.essenza.draco.modules.sales.domain.dto.product_order.CreateProductOrderDto;
//import com.essenza.draco.modules.sales.domain.dto.product_order.ProductOrderDto;
//import com.essenza.draco.modules.sales.domain.dto.product_order.UpdateProductOrderDto;
//import com.essenza.draco.modules.sales.application.input.product_order.CreateProductOrderUseCase;
//import com.essenza.draco.modules.sales.application.input.product_order.UpdateProductOrderUseCase;
//import com.essenza.draco.modules.sales.application.input.product_order.DeleteProductOrderUseCase;
//import com.essenza.draco.modules.sales.application.input.product_order.FindProductOrderByIdUseCase;
//import com.essenza.draco.modules.sales.application.input.product_order.FindProductOrdersUseCase;
//import org.springframework.graphql.data.method.annotation.Argument;
//import org.springframework.graphql.data.method.annotation.MutationMapping;
//import org.springframework.graphql.data.method.annotation.QueryMapping;
//import org.springframework.stereotype.Controller;
//
//import java.util.List;
//
//@Controller
//public class ProductOrderGraphQLController {
//
//    private final CreateProductOrderUseCase createProductOrder;
//    private final UpdateProductOrderUseCase updateProductOrder;
//    private final DeleteProductOrderUseCase deleteProductOrder;
//    private final FindProductOrderByIdUseCase findProductOrderById;
//    private final FindProductOrdersUseCase findProductOrders;
//
//    public ProductOrderGraphQLController(CreateProductOrderUseCase createProductOrder,
//        UpdateProductOrderUseCase updateProductOrder,
//        DeleteProductOrderUseCase deleteProductOrder,
//        FindProductOrderByIdUseCase findProductOrderById,
//        FindProductOrdersUseCase findProductOrders) {
//        this.createProductOrder = createProductOrder;
//        this.updateProductOrder = updateProductOrder;
//        this.deleteProductOrder = deleteProductOrder;
//        this.findProductOrderById = findProductOrderById;
//        this.findProductOrders = findProductOrders;
//    }
//
//    @QueryMapping
//    public ProductOrderDto productOrder(@Argument Long id) {
//        return findProductOrderById.findById(id).orElse(null);
//    }
//
//    @QueryMapping
//    public List<ProductOrderDto> productOrders() {
//        return findProductOrders.findAll();
//    }
//
//    @MutationMapping(name = "createProductOrder")
//    public ProductOrderDto createProductOrder(@Argument("productOrder") CreateProductOrderDto input) {
//        return createProductOrder.create(input);
//    }
//
//    @MutationMapping(name = "updateProductOrder")
//    public ProductOrderDto updateProductOrder(@Argument Long id, @Argument("productOrder") UpdateProductOrderDto input) {
//        return updateProductOrder.update(id, input);
//    }
//
//    @MutationMapping
//    public boolean deleteProductOrder(@Argument Long id) {
//        return deleteProductOrder.deleteById(id);
//    }
//}
