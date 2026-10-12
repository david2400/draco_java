package com.essenza.draco.modules.catalog.application.services.product_child;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.input.product_child.BulkDeleteProductChildrenUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductChildRepository;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;

@Service
@Transactional
@RequiredArgsConstructor
public class BulkDeleteProductChildrenService implements BulkDeleteProductChildrenUseCase {

    private final ProductChildRepository productChildRepository;

    @Override
    public BulkOperationResult deleteAll(List<Long> ids) {
        List<Long> unique = ids.stream().distinct().toList();
        BulkOperationResult.Builder result = new BulkOperationResult.Builder(unique.size());
        for (Long id : unique) {
            if (productChildRepository.deleteById(id)) result.success();
            else result.failure(id, "Variante de producto no encontrada: " + id);
        }
        return result.build();
    }
}
