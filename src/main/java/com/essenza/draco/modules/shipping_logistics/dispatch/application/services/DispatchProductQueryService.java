package com.essenza.draco.modules.shipping_logistics.dispatch.application.services;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product.BulkDeleteDispatchProductsUseCase;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product.SearchDispatchProductsUseCase;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.DispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.repositories.dispatch_product.DispatchProductRepositoryAdapter;

/**
 * Búsqueda paginada y borrado en lote de despachos (pantallas de
 * administración). Separado del servicio CRUD para no alterar su contrato.
 */
@Service
@Transactional
public class DispatchProductQueryService implements SearchDispatchProductsUseCase, BulkDeleteDispatchProductsUseCase {

    private final DispatchProductRepositoryAdapter repository;

    public DispatchProductQueryService(DispatchProductRepositoryAdapter repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DispatchProductDto> search(String query, Long orderId, String cityDestination, Pageable pageable) {
        return PageResponse.from(repository.search(query, orderId, cityDestination, pageable));
    }

    /** Elimina un despacho validando las reglas de negocio. */
    public void deleteChecked(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("No se encontró el despacho " + id + ".");
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
