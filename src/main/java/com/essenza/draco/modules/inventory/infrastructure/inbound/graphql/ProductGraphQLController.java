// package com.essenza.draco.modules.inventory.infrastructure.inbound.graphql;

// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.inventory.application.input.product.CreateProductUseCase;
// import com.essenza.draco.modules.inventory.application.input.product.UpdateProductUseCase;
// import com.essenza.draco.modules.inventory.application.input.product.DeleteProductUseCase;
// import com.essenza.draco.modules.inventory.application.input.product.FindProductByIdUseCase;
// import com.essenza.draco.modules.inventory.application.input.product.FindProductsUseCase;
// import com.essenza.draco.modules.inventory.application.input.product.FindProductsPageUseCase;
// import com.essenza.draco.modules.inventory.domain.dto.product.ProductDto;
// import com.essenza.draco.modules.inventory.domain.dto.product.CreateProductDto;
// import com.essenza.draco.modules.inventory.domain.dto.product.UpdateProductDto;
// import com.essenza.draco.modules.inventory.domain.dto.product.ProductFilter;

// import java.util.List;

// @Controller
// public class ProductGraphQLController {

//     private final CreateProductUseCase createProduct;
//     private final UpdateProductUseCase updateProduct;
//     private final DeleteProductUseCase deleteProduct;
//     private final FindProductByIdUseCase findProductById;
//     private final FindProductsUseCase findProducts;
//     private final FindProductsPageUseCase findProductsPage;

//     public ProductGraphQLController(CreateProductUseCase createProduct,
//         UpdateProductUseCase updateProduct,
//         DeleteProductUseCase deleteProduct,
//         FindProductByIdUseCase findProductById,
//         FindProductsUseCase findProducts,
//         FindProductsPageUseCase findProductsPage) {
//         this.createProduct = createProduct;
//         this.updateProduct = updateProduct;
//         this.deleteProduct = deleteProduct;
//         this.findProductById = findProductById;
//         this.findProducts = findProducts;
//         this.findProductsPage = findProductsPage;
//     }

//     @QueryMapping
//     public List<ProductDto> products(@Argument Integer page,
//                                      @Argument Integer size,
//                                      @Argument String sort,
//                                      @Argument ProductFilter filter) {
//         Pageable pageable = buildPageable(page, size, sort);
//         if (filter == null) return findProducts.findAll(pageable).getContent();
//         return findProductsPage.findAllPage(pageable, filter).getContent();
//     }

//     @QueryMapping
//     public ProductDto product(@Argument Long id) {
//         return findProductById.findById(id).orElse(null);
//     }

//     @MutationMapping(name = "createProduct")
//     public ProductDto createProduct(@Argument CreateProductDto input) {
//         return createProduct.create(input);
//     }

//     @MutationMapping(name = "updateProduct")
//     public ProductDto updateProduct(@Argument Long id, @Argument UpdateProductDto input) {
//         return updateProduct.update(id, input);
//     }

//     @MutationMapping
//     public Boolean deleteProduct(@Argument Long id) {
//         return deleteProduct.deleteById(id);
//     }

//     private Pageable buildPageable(Integer page, Integer size, String sortParam) {
//         int p = page != null ? Math.max(page, 0) : 0;
//         int s = size != null ? Math.max(size, 1) : 20;
//         Sort sort = parseSort(sortParam);
//         return PageRequest.of(p, s, sort);
//     }

//     private Sort parseSort(String sortParam) {
//         if (sortParam == null || sortParam.isBlank()) return Sort.unsorted();
//         // soporta "field,asc;other,desc"
//         String[] parts = sortParam.split(";");
//         Sort combined = Sort.unsorted();
//         for (String p : parts) {
//             String[] seg = p.trim().split(",");
//             if (seg.length == 0 || seg[0].isBlank()) continue;
//             String prop = seg[0].trim();
//             Sort.Direction dir = (seg.length > 1 && "desc".equalsIgnoreCase(seg[1].trim()))
//                     ? Sort.Direction.DESC : Sort.Direction.ASC;
//             Sort next = Sort.by(new Sort.Order(dir, prop));
//             combined = combined.isUnsorted() ? next : combined.and(next);
//         }
//         return combined;
//     }
// }
