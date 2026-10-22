package com.essenza.draco.modules.sales.application.services.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.essenza.draco.modules.sales.application.dto.order.BulkOrderStatusDto;
import com.essenza.draco.modules.sales.application.input.order.BulkChangeOrderStatusUseCase;
import com.essenza.draco.modules.sales.domain.model.OrderStatus;
import com.essenza.draco.modules.sales.domain.model.order.OrderEventType;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Cambio de estado en lote: cada orden en su propia transacción (una que falla no frena las demás). */
@Service
public class BulkOrderStatusService implements BulkChangeOrderStatusUseCase {

    private final OrderTransitions transitions;
    private final TransactionTemplate tx;

    public BulkOrderStatusService(OrderTransitions transitions, PlatformTransactionManager transactionManager) {
        this.transitions = transitions;
        this.tx = new TransactionTemplate(transactionManager);
    }

    @Override
    public BulkOperationResult changeAll(BulkOrderStatusDto input) {
        OrderStatus target = OrderStatus.parse(input.state());
        List<Long> ids = input.ids().stream().distinct().toList();
        BulkOperationResult.Builder result = new BulkOperationResult.Builder(ids.size());
        for (Long id : ids) {
            try {
                tx.executeWithoutResult(status ->
                        transitions.apply(id, target, input.reason(), OrderEventType.STATUS, null, input.actor()));
                result.success();
            } catch (NotFoundException | ConflictException | IllegalArgumentException ex) {
                result.failure(id, ex.getMessage());
            }
        }
        return result.build();
    }
}
