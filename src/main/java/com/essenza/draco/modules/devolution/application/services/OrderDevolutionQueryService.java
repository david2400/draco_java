package com.essenza.draco.modules.devolution.application.services;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import com.essenza.draco.modules.devolution.application.input.order_devolution.BulkDeleteOrderDevolutionsUseCase;
import com.essenza.draco.modules.devolution.application.input.order_devolution.SearchOrderDevolutionsUseCase;
import com.essenza.draco.modules.devolution.application.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.application.output.repository.OrderDevolutionRepository;

/**
 * Búsqueda paginada y borrado en lote de devolucións (pantallas de
 * administración). Separado del servicio CRUD para no alterar su contrato.
 */
@Service
@Transactional
public class OrderDevolutionQueryService implements SearchOrderDevolutionsUseCase, BulkDeleteOrderDevolutionsUseCase {

    private final OrderDevolutionRepository repository;

    public OrderDevolutionQueryService(OrderDevolutionRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderDevolutionDto> search(String query, String state, Long motiveDevolutionId, Long orderId, Pageable pageable) {
        return PageResponse.from(repository.search(query, state, motiveDevolutionId, orderId, pageable));
    }

    /** Elimina una devolución validando las reglas de negocio. */
    public void deleteChecked(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("No se encontró la devolución " + id + ".");
        }
        String state = repository.findById(id).map(dto -> dto.getState()).orElse("P");
        if (state != null && !"P".equals(state) && !"X".equals(state)) {
            throw new ConflictException("La devolución " + id
                    + " ya está en proceso: solo se pueden eliminar devoluciones pendientes o rechazadas.");
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
