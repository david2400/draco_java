// package com.essenza.draco.modules.inventory.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.inventory.application.input.product_child.CreateProductChildUseCase;
// import com.essenza.draco.modules.inventory.application.input.product_child.UpdateProductChildUseCase;
// import com.essenza.draco.modules.inventory.application.input.product_child.DeleteProductChildUseCase;
// import com.essenza.draco.modules.inventory.application.input.product_child.FindProductChildByIdUseCase;
// import com.essenza.draco.modules.inventory.application.input.product_child.FindProductChildrenUseCase;
// import com.essenza.draco.modules.inventory.domain.dto.product_child.ProductChildDto;
// import com.essenza.draco.modules.inventory.domain.dto.product_child.CreateProductChildDto;
// import com.essenza.draco.modules.inventory.domain.dto.product_child.UpdateProductChildDto;

// import java.util.List;

// @Controller
// public class ProductChildGraphQLController {

//     private final CreateProductChildUseCase createProductChild;
//     private final UpdateProductChildUseCase updateProductChild;
//     private final DeleteProductChildUseCase deleteProductChild;
//     private final FindProductChildByIdUseCase findProductChildById;
//     private final FindProductChildrenUseCase findProductChildren;

//     public ProductChildGraphQLController(CreateProductChildUseCase createProductChild,
//      UpdateProductChildUseCase updateProductChild,
//      DeleteProductChildUseCase deleteProductChild,
//      FindProductChildByIdUseCase findProductChildById,
//      FindProductChildrenUseCase findProductChildren) {
//         this.createProductChild = createProductChild;
//         this.updateProductChild = updateProductChild;
//         this.deleteProductChild = deleteProductChild;
//         this.findProductChildById = findProductChildById;
//         this.findProductChildren = findProductChildren;
//     }

//     @QueryMapping
//     public ProductChildDto productChild(@Argument Long id) {
//         return findProductChildById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<ProductChildDto> productChildren() {
//         return findProductChildren.findAll();
//     }

//     @MutationMapping(name = "createProductChild")
//     public ProductChildDto createProductChild(@Argument CreateProductChildDto input) {
//         return createProductChild.create(input);
//     }

//     @MutationMapping(name = "updateProductChild")
//     public ProductChildDto updateProductChild(@Argument Long id, @Argument UpdateProductChildDto input) {
//         return updateProductChild.update(id, input);
//     }

//     @MutationMapping
//     public Boolean deleteProductChild(@Argument Long id) {
//         return deleteProductChild.deleteById(id);
//     }
// }
