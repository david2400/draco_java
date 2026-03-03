//package com.essenza.draco.modules.sales.infrastructure.inbound.graphql;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.graphql.data.method.annotation.QueryMapping;
//import org.springframework.graphql.data.method.annotation.MutationMapping;
//import org.springframework.graphql.data.method.annotation.Argument;
//
//import com.essenza.draco.modules.sales.application.input.payment_type.CreatePaymentTypeUseCase;
//import com.essenza.draco.modules.sales.application.input.payment_type.UpdatePaymentTypeUseCase;
//import com.essenza.draco.modules.sales.application.input.payment_type.DeletePaymentTypeUseCase;
//import com.essenza.draco.modules.sales.application.input.payment_type.FindPaymentTypeByIdUseCase;
//import com.essenza.draco.modules.sales.application.input.payment_type.FindPaymentTypesUseCase;
//import com.essenza.draco.modules.sales.domain.dto.payment_type.PaymentTypeDto;
//import com.essenza.draco.modules.sales.domain.dto.payment_type.CreatePaymentTypeDto;
//import com.essenza.draco.modules.sales.domain.dto.payment_type.UpdatePaymentTypeDto;
//
//import java.util.List;
//
//@Controller
//public class PaymentTypeGraphQLController {
//
//    private final CreatePaymentTypeUseCase createPaymentType;
//    private final UpdatePaymentTypeUseCase updatePaymentType;
//    private final DeletePaymentTypeUseCase deletePaymentType;
//    private final FindPaymentTypeByIdUseCase findPaymentTypeById;
//    private final FindPaymentTypesUseCase findPaymentTypes;
//
//    public PaymentTypeGraphQLController(CreatePaymentTypeUseCase createPaymentType,
//       UpdatePaymentTypeUseCase updatePaymentType,
//       DeletePaymentTypeUseCase deletePaymentType,
//       FindPaymentTypeByIdUseCase findPaymentTypeById,
//       FindPaymentTypesUseCase findPaymentTypes) {
//        this.createPaymentType = createPaymentType;
//        this.updatePaymentType = updatePaymentType;
//        this.deletePaymentType = deletePaymentType;
//        this.findPaymentTypeById = findPaymentTypeById;
//        this.findPaymentTypes = findPaymentTypes;
//    }
//
//    @QueryMapping
//    public PaymentTypeDto paymentType(@Argument Long id) {
//        return findPaymentTypeById.findById(id).orElse(null);
//    }
//
//    @QueryMapping
//    public List<PaymentTypeDto> paymentTypes() {
//        return findPaymentTypes.findAll();
//    }
//
//    @MutationMapping(name = "createPaymentType")
//    public PaymentTypeDto createPaymentType(@Argument("paymentType") CreatePaymentTypeDto input) {
//        return createPaymentType.create(input);
//    }
//
//    @MutationMapping(name = "updatePaymentType")
//    public PaymentTypeDto updatePaymentType(@Argument Long id, @Argument("paymentType") UpdatePaymentTypeDto input) {
//        return updatePaymentType.update(id, input);
//    }
//
//    @MutationMapping
//    public boolean deletePaymentType(@Argument Long id) {
//        return deletePaymentType.deleteById(id);
//    }
//}
