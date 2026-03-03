// package com.essenza.draco.modules.dispatch.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.dispatch.application.input.dispatch_product.CreateDispatchProductUseCase;
// import com.essenza.draco.modules.dispatch.application.input.dispatch_product.UpdateDispatchProductUseCase;
// import com.essenza.draco.modules.dispatch.application.input.dispatch_product.DeleteDispatchProductUseCase;
// import com.essenza.draco.modules.dispatch.application.input.dispatch_product.FindDispatchProductByIdUseCase;
// import com.essenza.draco.modules.dispatch.application.input.dispatch_product.FindDispatchProductsUseCase;
// import com.essenza.draco.modules.dispatch.domain.dto.dispatch_product.DispatchProductDto;
// import com.essenza.draco.modules.dispatch.domain.dto.dispatch_product.CreateDispatchProductDto;
// import com.essenza.draco.modules.dispatch.domain.dto.dispatch_product.UpdateDispatchProductDto;

// import java.util.List;

// @Controller
// public class DispatchProductGraphQLController {

//     private final CreateDispatchProductUseCase createDispatchProduct;
//     private final UpdateDispatchProductUseCase updateDispatchProduct;
//     private final DeleteDispatchProductUseCase deleteDispatchProduct;
//     private final FindDispatchProductByIdUseCase findDispatchProductById;
//     private final FindDispatchProductsUseCase findDispatchProducts;

//     public DispatchProductGraphQLController(CreateDispatchProductUseCase createDispatchProduct,
//                                            UpdateDispatchProductUseCase updateDispatchProduct,
//                                            DeleteDispatchProductUseCase deleteDispatchProduct,
//                                            FindDispatchProductByIdUseCase findDispatchProductById,
//                                            FindDispatchProductsUseCase findDispatchProducts) {
//         this.createDispatchProduct = createDispatchProduct;
//         this.updateDispatchProduct = updateDispatchProduct;
//         this.deleteDispatchProduct = deleteDispatchProduct;
//         this.findDispatchProductById = findDispatchProductById;
//         this.findDispatchProducts = findDispatchProducts;
//     }

//     @MutationMapping(name = "createDispatchProduct")
//     public DispatchProductDto createDispatchProduct(@Argument("dispatchProduct") CreateDispatchProductDto input) {
//         return createDispatchProduct.create(input);
//     }

//     @MutationMapping(name = "updateDispatchProduct")
//     public DispatchProductDto updateDispatchProduct(@Argument Long id, @Argument("dispatchProduct") UpdateDispatchProductDto input) {
//         return updateDispatchProduct.update(id, input);
//     }

//     @MutationMapping
//     public boolean deleteDispatchProduct(@Argument Long id) {
//         return deleteDispatchProduct.deleteById(id);
//     }

//     @QueryMapping
//     public DispatchProductDto dispatchProduct(@Argument Long id) {
//         return findDispatchProductById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<DispatchProductDto> dispatchProducts() {
//         return findDispatchProducts.findAll();
//     }
// }
