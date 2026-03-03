// package com.essenza.draco.modules.dispatch.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.dispatch.application.input.tracking.CreateTrackingUseCase;
// import com.essenza.draco.modules.dispatch.application.input.tracking.UpdateTrackingUseCase;
// import com.essenza.draco.modules.dispatch.application.input.tracking.DeleteTrackingUseCase;
// import com.essenza.draco.modules.dispatch.application.input.tracking.FindTrackingByIdUseCase;
// import com.essenza.draco.modules.dispatch.application.input.tracking.FindTrackingsUseCase;
// import com.essenza.draco.modules.dispatch.domain.dto.tracking.TrackingDto;
// import com.essenza.draco.modules.dispatch.domain.dto.tracking.CreateTrackingDto;
// import com.essenza.draco.modules.dispatch.domain.dto.tracking.UpdateTrackingDto;

// import java.util.List;

// @Controller
// public class TrackingGraphQLController {

//     private final CreateTrackingUseCase createTracking;
//     private final UpdateTrackingUseCase updateTracking;
//     private final DeleteTrackingUseCase deleteTracking;
//     private final FindTrackingByIdUseCase findTrackingById;
//     private final FindTrackingsUseCase findTrackings;

//     public TrackingGraphQLController(CreateTrackingUseCase createTracking,
//                                     UpdateTrackingUseCase updateTracking,
//                                     DeleteTrackingUseCase deleteTracking,
//                                     FindTrackingByIdUseCase findTrackingById,
//                                     FindTrackingsUseCase findTrackings) {
//         this.createTracking = createTracking;
//         this.updateTracking = updateTracking;
//         this.deleteTracking = deleteTracking;
//         this.findTrackingById = findTrackingById;
//         this.findTrackings = findTrackings;
//     }

//     @MutationMapping(name = "createTracking")
//     public TrackingDto createTracking(@Argument("tracking") CreateTrackingDto input) {
//         return createTracking.create(input);
//     }

//     @MutationMapping(name = "updateTracking")
//     public TrackingDto updateTracking(@Argument Long id, @Argument("tracking") UpdateTrackingDto input) {
//         return updateTracking.update(id, input);
//     }

//     @MutationMapping
//     public boolean deleteTracking(@Argument Long id) {
//         return deleteTracking.deleteById(id);
//     }

//     @QueryMapping
//     public TrackingDto tracking(@Argument Long id) {
//         return findTrackingById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<TrackingDto> trackings() {
//         return findTrackings.findAll();
//     }
// }
