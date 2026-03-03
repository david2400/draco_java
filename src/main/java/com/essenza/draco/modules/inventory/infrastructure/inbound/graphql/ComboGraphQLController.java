// package com.essenza.draco.modules.inventory.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.inventory.application.input.combo.CreateComboUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.UpdateComboUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.DeleteComboUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.FindComboByIdUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.FindCombosUseCase;
// import com.essenza.draco.modules.inventory.domain.dto.combo.ComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.combo.CreateComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.combo.UpdateComboDto;

// import java.util.List;

// @Controller
// public class ComboGraphQLController {

//     private final CreateComboUseCase createCombo;
//     private final UpdateComboUseCase updateCombo;
//     private final DeleteComboUseCase deleteCombo;
//     private final FindComboByIdUseCase findComboById;
//     private final FindCombosUseCase findCombos;

//     public ComboGraphQLController(CreateComboUseCase createCombo,
//       UpdateComboUseCase updateCombo,
//       DeleteComboUseCase deleteCombo,
//       FindComboByIdUseCase findComboById,
//       FindCombosUseCase findCombos) {
//         this.createCombo = createCombo;
//         this.updateCombo = updateCombo;
//         this.deleteCombo = deleteCombo;
//         this.findComboById = findComboById;
//         this.findCombos = findCombos;
//     }

//     @QueryMapping
//     public ComboDto combo(@Argument Long id) {
//         return findComboById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<ComboDto> combos() {
//         return findCombos.findAll();
//     }

//     @MutationMapping(name = "createCombo")
//     public ComboDto createCombo(@Argument CreateComboDto input) {
//         return createCombo.create(input);
//     }

//     @MutationMapping(name = "updateCombo")
//     public ComboDto updateCombo(@Argument Long id, @Argument UpdateComboDto input) {
//         return updateCombo.update(id, input);
//     }

//     @MutationMapping
//     public Boolean deleteCombo(@Argument Long id) {
//         return deleteCombo.deleteById(id);
//     }
// }
