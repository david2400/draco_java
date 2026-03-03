// package com.essenza.draco.modules.devolution.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.devolution.application.input.motive_devolution.CreateMotiveDevolutionUseCase;
// import com.essenza.draco.modules.devolution.application.input.motive_devolution.UpdateMotiveDevolutionUseCase;
// import com.essenza.draco.modules.devolution.application.input.motive_devolution.DeleteMotiveDevolutionUseCase;
// import com.essenza.draco.modules.devolution.application.input.motive_devolution.FindMotiveDevolutionByIdUseCase;
// import com.essenza.draco.modules.devolution.application.input.motive_devolution.FindMotiveDevolutionsUseCase;
// import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.MotiveDevolutionDto;
// import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.CreateMotiveDevolutionDto;
// import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.UpdateMotiveDevolutionDto;

// import java.util.List;

// @Controller
// public class MotiveDevolutionGraphQLController {

//     private final CreateMotiveDevolutionUseCase createMotiveDevolution;
//     private final UpdateMotiveDevolutionUseCase updateMotiveDevolution;
//     private final DeleteMotiveDevolutionUseCase deleteMotiveDevolution;
//     private final FindMotiveDevolutionByIdUseCase findMotiveDevolutionById;
//     private final FindMotiveDevolutionsUseCase findMotiveDevolutions;

//     public MotiveDevolutionGraphQLController(CreateMotiveDevolutionUseCase createMotiveDevolution,
//                                             UpdateMotiveDevolutionUseCase updateMotiveDevolution,
//                                             DeleteMotiveDevolutionUseCase deleteMotiveDevolution,
//                                             FindMotiveDevolutionByIdUseCase findMotiveDevolutionById,
//                                             FindMotiveDevolutionsUseCase findMotiveDevolutions) {
//         this.createMotiveDevolution = createMotiveDevolution;
//         this.updateMotiveDevolution = updateMotiveDevolution;
//         this.deleteMotiveDevolution = deleteMotiveDevolution;
//         this.findMotiveDevolutionById = findMotiveDevolutionById;
//         this.findMotiveDevolutions = findMotiveDevolutions;
//     }

//     @MutationMapping(name = "createMotiveDevolution")
//     public MotiveDevolutionDto createMotiveDevolution(@Argument("motiveDevolution") CreateMotiveDevolutionDto input) {
//         return createMotiveDevolution.create(input);
//     }

//     @MutationMapping(name = "updateMotiveDevolution")
//     public MotiveDevolutionDto updateMotiveDevolution(@Argument Long id, @Argument("motiveDevolution") UpdateMotiveDevolutionDto input) {
//         return updateMotiveDevolution.update(id, input);
//     }

//     @MutationMapping
//     public boolean deleteMotiveDevolution(@Argument Long id) {
//         return deleteMotiveDevolution.deleteById(id);
//     }

//     @QueryMapping
//     public MotiveDevolutionDto motiveDevolution(@Argument Long id) {
//         return findMotiveDevolutionById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<MotiveDevolutionDto> motiveDevolutions() {
//         return findMotiveDevolutions.findAll();
//     }
// }
