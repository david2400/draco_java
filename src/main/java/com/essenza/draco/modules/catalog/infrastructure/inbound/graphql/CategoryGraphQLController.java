// package com.essenza.draco.modules.catalog.infrastructure.inbound.graphql;

// import com.essenza.draco.modules.catalog.domain.dto.category.CategoryDto;
// import com.essenza.draco.modules.catalog.domain.dto.category.CreateCategoryDto;
// import com.essenza.draco.modules.catalog.domain.dto.category.UpdateCategoryDto;
// import com.essenza.draco.modules.catalog.application.input.category.CreateCategoryUseCase;
// import com.essenza.draco.modules.catalog.application.input.category.UpdateCategoryUseCase;
// import com.essenza.draco.modules.catalog.application.input.category.DeleteCategoryByIdUseCase;
// import com.essenza.draco.modules.catalog.application.input.category.FindCategoryByIdUseCase;
// import com.essenza.draco.modules.catalog.application.input.category.FindAllCategoriesUseCase;
// import org.springframework.graphql.data.method.annotation.Argument;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.stereotype.Controller;

// import java.util.List;

// @Controller
// public class CategoryGraphQLController {

//     private final CreateCategoryUseCase createCategory;
//     private final UpdateCategoryUseCase updateCategory;
//     private final DeleteCategoryByIdUseCase deleteCategoryById;
//     private final FindCategoryByIdUseCase findCategoryById;
//     private final FindAllCategoriesUseCase findAllCategories;

//     public CategoryGraphQLController(CreateCategoryUseCase createCategory,
//                                      UpdateCategoryUseCase updateCategory,
//                                      DeleteCategoryByIdUseCase deleteCategoryById,
//                                      FindCategoryByIdUseCase findCategoryById,
//                                      FindAllCategoriesUseCase findAllCategories) {
//         this.createCategory = createCategory;
//         this.updateCategory = updateCategory;
//         this.deleteCategoryById = deleteCategoryById;
//         this.findCategoryById = findCategoryById;
//         this.findAllCategories = findAllCategories;
//     }

//     @MutationMapping(name = "createCategory")
//     public CategoryDto createCategory(@Argument("category") CreateCategoryDto input) {
//         return createCategory.create(input);
//     }

//     @MutationMapping(name = "updateCategory")
//     public CategoryDto updateCategory(@Argument Long id, @Argument("category") UpdateCategoryDto input) {
//         return updateCategory.update(id, input);
//     }

//     @QueryMapping
//     public CategoryDto category(@Argument Long id) {
//         return findCategoryById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<CategoryDto> categories() {
//         return findAllCategories.findAll();
//     }
// }
