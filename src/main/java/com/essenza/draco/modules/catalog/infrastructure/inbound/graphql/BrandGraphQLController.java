// package com.essenza.draco.modules.catalog.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.catalog.application.input.brand.CreateBrandUseCase;
// import com.essenza.draco.modules.catalog.application.input.brand.UpdateBrandUseCase;
// import com.essenza.draco.modules.catalog.application.input.brand.DeleteBrandByIdUseCase;
// import com.essenza.draco.modules.catalog.application.input.brand.FindBrandByIdUseCase;
// import com.essenza.draco.modules.catalog.application.input.brand.FindAllBrandsUseCase;
// import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;
// import com.essenza.draco.modules.catalog.domain.dto.brand.CreateBrandDto;
// import com.essenza.draco.modules.catalog.domain.dto.brand.UpdateBrandDto;

// import java.util.List;

// @Controller
// public class BrandGraphQLController {

//     private final CreateBrandUseCase createBrand;
//     private final UpdateBrandUseCase updateBrand;
//     private final DeleteBrandByIdUseCase deleteBrandById;
//     private final FindBrandByIdUseCase findBrandById;
//     private final FindAllBrandsUseCase findAllBrands;

//     public BrandGraphQLController(CreateBrandUseCase createBrand,
//                                   UpdateBrandUseCase updateBrand,
//                                   DeleteBrandByIdUseCase deleteBrandById,
//                                   FindBrandByIdUseCase findBrandById,
//                                   FindAllBrandsUseCase findAllBrands) {
//         this.createBrand = createBrand;
//         this.updateBrand = updateBrand;
//         this.deleteBrandById = deleteBrandById;
//         this.findBrandById = findBrandById;
//         this.findAllBrands = findAllBrands;
//     }

//     @MutationMapping(name = "createBrand")
//     public BrandDto createBrand(@Argument("brand") CreateBrandDto input) {
//         return createBrand.create(input);
//     }

//     @MutationMapping(name = "updateBrand")
//     public BrandDto updateBrand(@Argument Long id, @Argument("brand") UpdateBrandDto input) {
//         return updateBrand.update(id, input);
//     }

//     @QueryMapping
//     public BrandDto brand(@Argument Long id) {
//         return findBrandById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<BrandDto> brands() {
//         return findAllBrands.findAll();
//     }
// }

