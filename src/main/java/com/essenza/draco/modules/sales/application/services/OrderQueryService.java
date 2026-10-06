package com.essenza.draco.modules.sales.application.services;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import com.essenza.draco.modules.sales.application.input.order.BulkDeleteOrdersUseCase;
import com.essenza.draco.modules.sales.application.input.order.SearchOrdersUseCase;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.output.repository.OrderRepository;

/**
 * Búsqueda paginada y borrado en lote de ordens (pantallas de
 * administración). Separado del servicio CRUD para no alterar su contrato.
 */
@Service
@Transactional
public class OrderQueryService implements SearchOrdersUseCase, BulkDeleteOrdersUseCase {

    private final OrderRepository repository;
    private final com.essenza.draco.modules.sales.application.input.order.DeleteOrderUseCase deleteOrder;

    public OrderQueryService(OrderRepository repository,
                             com.essenza.draco.modules.sales.application.input.order.DeleteOrderUseCase deleteOrder) {
        this.repository = repository;
        this.deleteOrder = deleteOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderDto> search(String query, String state, Pageable pageable) {
        return PageResponse.from(repository.search(query, state, pageable));
    }

    /** Elimina una orden validando las reglas de negocio. */
    public void deleteChecked(Long id) {
        // Mismas reglas que el borrado individual (dependencias y stock reservado).
        deleteOrder.deleteById(id);
    }

    @Override
    public BulkOperationResult deleteAll(List<Long> ids) {
        List<Long> unique = ids.stream().distinct().toList();
        BulkOperationResult.Builder result = new BulkOperationResult.Builder(unique.size());
        for (Long id : unique) {
            try {
                deleteChecked(id);
                result.success();
            } catch (NotFoundException | ConflictException ex) {
                result.failure(id, ex.getMessage());
            }
        }
        return result.build();
    }
}
