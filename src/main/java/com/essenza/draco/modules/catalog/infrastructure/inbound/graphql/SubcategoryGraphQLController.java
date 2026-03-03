// package com.essenza.draco.modules.catalog.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.catalog.application.input.subcategory.CreateSubcategoryUseCase;
// import com.essenza.draco.modules.catalog.application.input.subcategory.UpdateSubcategoryUseCase;
// import com.essenza.draco.modules.catalog.application.input.subcategory.DeleteSubcategoryByIdUseCase;
// import com.essenza.draco.modules.catalog.application.input.subcategory.FindSubcategoryByIdUseCase;
// import com.essenza.draco.modules.catalog.application.input.subcategory.FindAllSubcategoriesUseCase;
// import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;
// import com.essenza.draco.modules.catalog.domain.dto.subcategory.CreateSubcategoryDto;
// import com.essenza.draco.modules.catalog.domain.dto.subcategory.UpdateSubcategoryDto;

// import java.util.List;

// @Controller
// public class SubcategoryGraphQLController {

//     private final CreateSubcategoryUseCase createSubcategory;
//     private final UpdateSubcategoryUseCase updateSubcategory;
//     private final DeleteSubcategoryByIdUseCase deleteSubcategoryById;
//     private final FindSubcategoryByIdUseCase findSubcategoryById;
//     private final FindAllSubcategoriesUseCase findAllSubcategories;

//     public SubcategoryGraphQLController(CreateSubcategoryUseCase createSubcategory,
//                                         UpdateSubcategoryUseCase updateSubcategory,
//                                         DeleteSubcategoryByIdUseCase deleteSubcategoryById,
//                                         FindSubcategoryByIdUseCase findSubcategoryById,
//                                         FindAllSubcategoriesUseCase findAllSubcategories) {
//         this.createSubcategory = createSubcategory;
//         this.updateSubcategory = updateSubcategory;
//         this.deleteSubcategoryById = deleteSubcategoryById;
//         this.findSubcategoryById = findSubcategoryById;
//         this.findAllSubcategories = findAllSubcategories;
//     }

//     @MutationMapping(name = "createSubcategory")
//     public SubcategoryDto createSubcategory(@Argument("subcategory") CreateSubcategoryDto input) {
//         return createSubcategory.create(input);
//     }

//     @MutationMapping(name = "updateSubcategory")
//     public SubcategoryDto updateSubcategory(@Argument Long id, @Argument("subcategory") UpdateSubcategoryDto input) {
//         return updateSubcategory.update(id, input);
//     }

//     @QueryMapping
//     public SubcategoryDto subcategory(@Argument Long id) {
//         return findSubcategoryById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<SubcategoryDto> subcategories() {
//         return findAllSubcategories.findAll();
//     }
// }
