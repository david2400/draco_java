// package com.essenza.draco.modules.product_details.infrastructure.inbound.graphql;

// import org.springframework.stereotype.Controller;
// import org.springframework.graphql.data.method.annotation.QueryMapping;
// import org.springframework.graphql.data.method.annotation.MutationMapping;
// import org.springframework.graphql.data.method.annotation.Argument;

// import com.essenza.draco.modules.product_details.application.input.unit_measurement.CreateUnitMeasurementUseCase;
// import com.essenza.draco.modules.product_details.application.input.unit_measurement.UpdateUnitMeasurementUseCase;
// import com.essenza.draco.modules.product_details.application.input.unit_measurement.DeleteUnitMeasurementUseCase;
// import com.essenza.draco.modules.product_details.application.input.unit_measurement.FindUnitMeasurementByIdUseCase;
// import com.essenza.draco.modules.product_details.application.input.unit_measurement.FindUnitMeasurementsUseCase;
// import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UnitMeasurementDto;
// import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.CreateUnitMeasurementDto;
// import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UpdateUnitMeasurementDto;

// import java.util.List;

// @Controller
// public class UnitMeasurementGraphQLController {

//     private final CreateUnitMeasurementUseCase createUnitMeasurement;
//     private final UpdateUnitMeasurementUseCase updateUnitMeasurement;
//     private final DeleteUnitMeasurementUseCase deleteUnitMeasurement;
//     private final FindUnitMeasurementByIdUseCase findUnitMeasurementById;
//     private final FindUnitMeasurementsUseCase findUnitMeasurements;

//     public UnitMeasurementGraphQLController(CreateUnitMeasurementUseCase createUnitMeasurement,
//        UpdateUnitMeasurementUseCase updateUnitMeasurement,
//        DeleteUnitMeasurementUseCase deleteUnitMeasurement,
//        FindUnitMeasurementByIdUseCase findUnitMeasurementById,
//        FindUnitMeasurementsUseCase findUnitMeasurements) {
//         this.createUnitMeasurement = createUnitMeasurement;
//         this.updateUnitMeasurement = updateUnitMeasurement;
//         this.deleteUnitMeasurement = deleteUnitMeasurement;
//         this.findUnitMeasurementById = findUnitMeasurementById;
//         this.findUnitMeasurements = findUnitMeasurements;
//     }

//     @QueryMapping
//     public UnitMeasurementDto unit(@Argument Long id) {
//         return findUnitMeasurementById.findById(id).orElse(null);
//     }

//     @QueryMapping
//     public List<UnitMeasurementDto> units() {
//         return findUnitMeasurements.findAll();
//     }

//     @MutationMapping(name = "createUnitMeasurement")
//     public UnitMeasurementDto createUnitMeasurement(@Argument("unitMeasurement") CreateUnitMeasurementDto input) {
//         return createUnitMeasurement.create(input);
//     }

//     @MutationMapping(name = "updateUnitMeasurement")
//     public UnitMeasurementDto updateUnitMeasurement(@Argument Long id, @Argument("unitMeasurement") UpdateUnitMeasurementDto input) {
//         return updateUnitMeasurement.update(id, input);
//     }

//     @MutationMapping
//     public Boolean deleteUnitMeasurement(@Argument Long id) {
//         return deleteUnitMeasurement.deleteById(id);
//     }
// }
