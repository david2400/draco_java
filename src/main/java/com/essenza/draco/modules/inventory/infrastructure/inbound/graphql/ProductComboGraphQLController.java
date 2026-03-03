// package com.essenza.draco.modules.inventory.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.inventory.application.input.product_combo.CreateProductComboUseCase;
// import com.essenza.draco.modules.inventory.application.input.product_combo.UpdateProductComboUseCase;
// import com.essenza.draco.modules.inventory.application.input.product_combo.DeleteProductComboUseCase;
// import com.essenza.draco.modules.inventory.application.input.product_combo.FindProductComboByIdUseCase;
// import com.essenza.draco.modules.inventory.application.input.product_combo.FindProductCombosUseCase;
// import com.essenza.draco.modules.inventory.domain.dto.product_combo.ProductComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.product_combo.CreateProductComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.product_combo.UpdateProductComboDto;

// import java.util.List;

// @Controller
// public class ProductComboGraphQLController {

//     private final CreateProductComboUseCase createProductCombo;
//     private final UpdateProductComboUseCase updateProductCombo;
//     private final DeleteProductComboUseCase deleteProductCombo;
//     private final FindProductComboByIdUseCase findProductComboById;
//     private final FindProductCombosUseCase findProductCombos;

//     public ProductComboGraphQLController(CreateProductComboUseCase createProductCombo,
//         UpdateProductComboUseCase updateProductCombo,
//         DeleteProductComboUseCase deleteProductCombo,
//         FindProductComboByIdUseCase findProductComboById,
//         FindProductCombosUseCase findProductCombos) {
//         this.createProductCombo = createProductCombo;
//         this.updateProductCombo = updateProductCombo;
//         this.deleteProductCombo = deleteProductCombo;
//         this.findProductComboById = findProductComboById;
//         this.findProductCombos = findProductCombos;
//     }

//     @QueryMapping
//     public ProductComboDto productCombo(@Argument Long id) {
//         return findProductComboById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<ProductComboDto> productCombos() {
//         return findProductCombos.findAll();
//     }

//     @MutationMapping(name = "createProductCombo")
//     public ProductComboDto createProductCombo(@Argument CreateProductComboDto input) {
//         return createProductCombo.create(input);
//     }

//     @MutationMapping(name = "updateProductCombo")
//     public ProductComboDto updateProductCombo(@Argument Long id, @Argument UpdateProductComboDto input) {
//         return updateProductCombo.update(id, input);
//     }

//     @MutationMapping
//     public Boolean deleteProductCombo(@Argument Long id) {
//         return deleteProductCombo.deleteById(id);
//     }
// }
