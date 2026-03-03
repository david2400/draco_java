package com.essenza.draco.shared.common.domain.mapper;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class NumberMapper {
    public BigDecimal toBigDecimal(Double value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }
    public Double toDouble(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }
}
