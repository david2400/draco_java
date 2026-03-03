// package com.essenza.draco.modules.product_details.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.product_details.application.input.type_product.CreateTypeProductUseCase;
// import com.essenza.draco.modules.product_details.application.input.type_product.UpdateTypeProductUseCase;
// import com.essenza.draco.modules.product_details.application.input.type_product.DeleteTypeProductUseCase;
// import com.essenza.draco.modules.product_details.application.input.type_product.FindTypeProductByIdUseCase;
// import com.essenza.draco.modules.product_details.application.input.type_product.FindTypeProductsUseCase;
// import com.essenza.draco.modules.product_details.domain.dto.type_product.TypeProductDto;
// import com.essenza.draco.modules.product_details.domain.dto.type_product.CreateTypeProductDto;
// import com.essenza.draco.modules.product_details.domain.dto.type_product.UpdateTypeProductDto;

// import java.util.List;

// @Controller
// public class TypeProductGraphQLController {

//     private final CreateTypeProductUseCase createTypeProduct;
//     private final UpdateTypeProductUseCase updateTypeProduct;
//     private final DeleteTypeProductUseCase deleteTypeProduct;
//     private final FindTypeProductByIdUseCase findTypeProductById;
//     private final FindTypeProductsUseCase findTypeProducts;

//     public TypeProductGraphQLController(CreateTypeProductUseCase createTypeProduct,
//        UpdateTypeProductUseCase updateTypeProduct,
//        DeleteTypeProductUseCase deleteTypeProduct,
//        FindTypeProductByIdUseCase findTypeProductById,
//        FindTypeProductsUseCase findTypeProducts) {
//         this.createTypeProduct = createTypeProduct;
//         this.updateTypeProduct = updateTypeProduct;
//         this.deleteTypeProduct = deleteTypeProduct;
//         this.findTypeProductById = findTypeProductById;
//         this.findTypeProducts = findTypeProducts;
//     }

//     @QueryMapping
//     public TypeProductDto typeProduct(@Argument Long id) {
//         return findTypeProductById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<TypeProductDto> typeProducts() {
//         return findTypeProducts.findAll();
//     }

//     @MutationMapping(name = "createTypeProduct")
//     public TypeProductDto createTypeProduct(@Argument("typeProduct") CreateTypeProductDto input) {
//         return createTypeProduct.create(input);
//     }

//     @MutationMapping(name = "updateTypeProduct")
//     public TypeProductDto updateTypeProduct(@Argument Long id, @Argument("typeProduct") UpdateTypeProductDto input) {
//         return updateTypeProduct.update(id, input);
//     }

//     @MutationMapping
//     public Boolean deleteTypeProduct(@Argument Long id) {
//         return deleteTypeProduct.deleteById(id);
//     }
// }
