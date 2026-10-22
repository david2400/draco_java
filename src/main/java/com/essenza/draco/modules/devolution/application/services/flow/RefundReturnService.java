package com.essenza.draco.modules.devolution.application.services.flow;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.devolution.application.dto.flow.RefundReturnDto;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.input.flow.RefundReturnUseCase;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;
import com.essenza.draco.shared.common.devolution.DevolutionRefundedEvent;
import com.essenza.draco.shared.common.sales.OrderReturns;

/**
 * Reembolso: acredita el valor a la orden, devuelve el dinero sobre sus pagos y publica
 * {@link DevolutionRefundedEvent} (facturación emite la nota crédito parcial tras el commit).
 */
@Service
@Transactional
public class RefundReturnService implements RefundReturnUseCase {

    private final ReturnSoporte soporte;
    private final OrderReturns orders;
    private final ApplicationEventPublisher events;

    public RefundReturnService(ReturnSoporte soporte, OrderReturns orders, ApplicationEventPublisher events) {
        this.soporte = soporte;
        this.orders = orders;
        this.events = events;
    }

    @Override
    public OrderDevolutionDto refund(Long id, RefundReturnDto input) {
        ReturnCase returnCase = soporte.locked(id);
        Long method = input == null ? null : input.refundMethodId();
        soporte.checkCatalogs(null, null, method);
        BigDecimal amount = returnCase.refundAmountFor(input == null ? null : input.amount());
        String reason = "Devolución #" + returnCase.id()
                + (input == null || input.notes() == null || input.notes().isBlank() ? "" : " · " + input.notes().trim());
        OrderReturns.RefundResult result = orders.refund(returnCase.orderId(), returnCase.creditValue(), amount, reason);
        String detail = result.payments().isEmpty() ? null : result.payments().stream()
                .map(p -> p.reference() + (p.method() == null ? "" : " (" + p.method() + ")") + ": " + p.amount().toPlainString())
                .collect(Collectors.joining("; "));
        returnCase.markRefunded(input == null ? null : input.by(), method, amount, detail, ReturnSoporte.now());
        ReturnCase saved = soporte.save(returnCase);
        List<DevolutionRefundedEvent.Line> lines = saved.items().stream()
                .filter(item -> item.received() > 0 && item.refundAmount() != null && item.refundAmount().signum() > 0)
                .map(item -> new DevolutionRefundedEvent.Line(item.productOrderId(), item.received(), item.refundAmount()))
                .toList();
        if (!lines.isEmpty()) {
            events.publishEvent(new DevolutionRefundedEvent(saved.id(), saved.orderId(), reason, lines));
        }
        return soporte.toDto(saved);
    }
}
