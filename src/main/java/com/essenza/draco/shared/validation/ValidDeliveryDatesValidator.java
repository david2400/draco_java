package com.essenza.draco.shared.validation;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.CreateDispatchProductDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ValidDeliveryDatesValidator implements ConstraintValidator<ValidDeliveryDates, CreateDispatchProductDto> {
    @Override
    public boolean isValid(CreateDispatchProductDto value, ConstraintValidatorContext context) {
        if (value == null) return true;
        LocalDate estimated = value.getEstimatedDeliveryDate();
        LocalDate real = value.getRealDeliveryDate();
        // If any is null, leave to other @NotNull validations
        if (estimated == null || real == null) return true;
        // real must be same day or after estimated
        return !real.isBefore(estimated);
    }
}
