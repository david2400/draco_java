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
import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;
import com.essenza.draco.modules.sales.infrastructure.outbound.repositories.order.OrderRepositoryAdapter;

/**
 * Búsqueda paginada y borrado en lote de ordens (pantallas de
 * administración). Separado del servicio CRUD para no alterar su contrato.
 */
@Service
@Transactional
public class OrderQueryService implements SearchOrdersUseCase, BulkDeleteOrdersUseCase {

    private final OrderRepositoryAdapter repository;

    public OrderQueryService(OrderRepositoryAdapter repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderDto> search(String query, String state, Pageable pageable) {
        return PageResponse.from(repository.search(query, state, pageable));
    }

    /** Elimina una orden validando las reglas de negocio. */
    public void deleteChecked(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("No se encontró la orden " + id + ".");
        }
        if (repository.hasDependents(id)) {
            throw new ConflictException("No se puede eliminar la orden " + id
                    + ": tiene despachos o devoluciones asociados.");
        }
        repository.deleteById(id);
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
