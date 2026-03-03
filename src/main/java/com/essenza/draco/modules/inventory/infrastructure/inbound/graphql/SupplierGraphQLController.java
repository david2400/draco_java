// package com.essenza.draco.modules.inventory.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.inventory.application.input.supplier.CreateSupplierUseCase;
// import com.essenza.draco.modules.inventory.application.input.supplier.UpdateSupplierUseCase;
// import com.essenza.draco.modules.inventory.application.input.supplier.DeleteSupplierUseCase;
// import com.essenza.draco.modules.inventory.application.input.supplier.FindSupplierByIdUseCase;
// import com.essenza.draco.modules.inventory.application.input.supplier.FindSuppliersUseCase;
// import com.essenza.draco.modules.inventory.domain.dto.supplier.SupplierDto;
// import com.essenza.draco.modules.inventory.domain.dto.supplier.CreateSupplierDto;
// import com.essenza.draco.modules.inventory.domain.dto.supplier.UpdateSupplierDto;

// import java.util.List;

// @Controller
// public class SupplierGraphQLController {

//     private final CreateSupplierUseCase createSupplier;
//     private final UpdateSupplierUseCase updateSupplier;
//     private final DeleteSupplierUseCase deleteSupplier;
//     private final FindSupplierByIdUseCase findSupplierById;
//     private final FindSuppliersUseCase findSuppliers;

//     public SupplierGraphQLController(CreateSupplierUseCase createSupplier,
//         UpdateSupplierUseCase updateSupplier,
//         DeleteSupplierUseCase deleteSupplier,
//         FindSupplierByIdUseCase findSupplierById,
//         FindSuppliersUseCase findSuppliers) {
//         this.createSupplier = createSupplier;
//         this.updateSupplier = updateSupplier;
//         this.deleteSupplier = deleteSupplier;
//         this.findSupplierById = findSupplierById;
//         this.findSuppliers = findSuppliers;
//     }

//     @QueryMapping
//     public SupplierDto supplier(@Argument Long id) {
//         return findSupplierById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<SupplierDto> suppliers() {
//         return findSuppliers.findAll();
//     }

//     @MutationMapping(name = "createSupplier")
//     public SupplierDto createSupplier(@Argument CreateSupplierDto input) {
//         return createSupplier.create(input);
//     }

//     @MutationMapping(name = "updateSupplier")
//     public SupplierDto updateSupplier(@Argument Long id, @Argument UpdateSupplierDto input) {
//         return updateSupplier.update(id, input);
//     }

//     @MutationMapping
//     public Boolean deleteSupplier(@Argument Long id) {
//         return deleteSupplier.deleteById(id);
//     }
// }
