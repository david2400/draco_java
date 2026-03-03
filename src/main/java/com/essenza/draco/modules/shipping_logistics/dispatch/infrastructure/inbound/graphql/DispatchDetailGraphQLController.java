// package com.essenza.draco.modules.dispatch.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.dispatch.application.input.dispatch_detail.CreateDispatchDetailUseCase;
// import com.essenza.draco.modules.dispatch.application.input.dispatch_detail.UpdateDispatchDetailUseCase;
// import com.essenza.draco.modules.dispatch.application.input.dispatch_detail.DeleteDispatchDetailUseCase;
// import com.essenza.draco.modules.dispatch.application.input.dispatch_detail.FindDispatchDetailByIdUseCase;
// import com.essenza.draco.modules.dispatch.application.input.dispatch_detail.FindDispatchDetailsUseCase;
// import com.essenza.draco.modules.dispatch.domain.dto.dispatch_detail.DispatchDetailDto;
// import com.essenza.draco.modules.dispatch.domain.dto.dispatch_detail.CreateDispatchDetailDto;
// import com.essenza.draco.modules.dispatch.domain.dto.dispatch_detail.UpdateDispatchDetailDto;

// import java.util.List;

// @Controller
// public class DispatchDetailGraphQLController {

//     private final CreateDispatchDetailUseCase createDispatchDetail;
//     private final UpdateDispatchDetailUseCase updateDispatchDetail;
//     private final DeleteDispatchDetailUseCase deleteDispatchDetail;
//     private final FindDispatchDetailByIdUseCase findDispatchDetailById;
//     private final FindDispatchDetailsUseCase findDispatchDetails;

//     public DispatchDetailGraphQLController(CreateDispatchDetailUseCase createDispatchDetail,
//                                           UpdateDispatchDetailUseCase updateDispatchDetail,
//                                           DeleteDispatchDetailUseCase deleteDispatchDetail,
//                                           FindDispatchDetailByIdUseCase findDispatchDetailById,
//                                           FindDispatchDetailsUseCase findDispatchDetails) {
//         this.createDispatchDetail = createDispatchDetail;
//         this.updateDispatchDetail = updateDispatchDetail;
//         this.deleteDispatchDetail = deleteDispatchDetail;
//         this.findDispatchDetailById = findDispatchDetailById;
//         this.findDispatchDetails = findDispatchDetails;
//     }

//     @MutationMapping(name = "createDispatchDetail")
//     public DispatchDetailDto createDispatchDetail(@Argument("dispatchDetail") CreateDispatchDetailDto input) {
//         return createDispatchDetail.create(input);
//     }

//     @MutationMapping(name = "updateDispatchDetail")
//     public DispatchDetailDto updateDispatchDetail(@Argument Long id, @Argument("dispatchDetail") UpdateDispatchDetailDto input) {
//         return updateDispatchDetail.update(id, input);
//     }

//     @MutationMapping
//     public boolean deleteDispatchDetail(@Argument Long id) {
//         return deleteDispatchDetail.deleteById(id);
//     }

//     @QueryMapping
//     public DispatchDetailDto dispatchDetail(@Argument Long id) {
//         return findDispatchDetailById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<DispatchDetailDto> dispatchDetails() {
//         return findDispatchDetails.findAll();
//     }
// }
