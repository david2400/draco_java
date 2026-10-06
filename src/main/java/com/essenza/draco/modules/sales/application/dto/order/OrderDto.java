package com.essenza.draco.modules.sales.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto extends AuditInfoDto {
    private Long id;
    private String complementaryOrder;
    private BigDecimal total;
    private String state;
    /** La orden reserva y descuenta stock (órdenes creadas desde la Fase 5). */
    private Boolean stockManaged;
    private String cancelReason;
    /** Estados a los que puede pasar (solo lectura). */
    private java.util.List<String> nextStates;
}
