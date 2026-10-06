package com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.repositories.dispatch_product;

import com.essenza.draco.modules.shipping_logistics.dispatch.application.output.repository.DispatchProductRepository;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.dto.dispatch_product.CreateDispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.dto.dispatch_product.DispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.dto.dispatch_product.UpdateDispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.mappers.DispatchProductMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DispatchProductRepositoryAdapter implements DispatchProductRepository {

    private final JpaDispatchProductRepository jpa;
    private final DispatchProductMapper mapper;

    public DispatchProductRepositoryAdapter(JpaDispatchProductRepository jpa, DispatchProductMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }


    @Override
    public DispatchProductDto create(CreateDispatchProductDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public DispatchProductDto update(Long id, UpdateDispatchProductDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DispatchProduct not found: " + id));
        mapper.updateEntityFromDto(input, entity);
        var updated = jpa.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }

    @Override
    public Optional<DispatchProductDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<DispatchProductDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }

    /**
     * Búsqueda paginada: {@code query} busca en guideNumber, address, cityOrigin, cityDestination
     * y, si es numérico, también por id / orderId.
     */
    public org.springframework.data.domain.Page<DispatchProductDto> search(String query, Long orderId, String cityDestination,
                                                               org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.persistence.mysql.shop.DispatchProductEntity> spec = (root, criteria, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (query != null && !query.isBlank()) {
                String like = "%" + query.trim().toLowerCase(java.util.Locale.ROOT) + "%";
                java.util.List<jakarta.persistence.criteria.Predicate> textual = new java.util.ArrayList<>(java.util.List.of(
                        cb.like(cb.lower(cb.coalesce(root.<String>get("guideNumber"), "")), like),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("address"), "")), like),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("cityOrigin"), "")), like),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("cityDestination"), "")), like)));
                if (query.trim().matches("\\d{1,18}")) {
                    Long number = Long.valueOf(query.trim());
                    textual.add(cb.equal(root.get("id"), number));
                    textual.add(cb.equal(root.get("orderId"), number));
                }
                predicates.add(cb.or(textual.toArray(jakarta.persistence.criteria.Predicate[]::new)));
            }
            if (orderId != null) {
                predicates.add(cb.equal(root.get("orderId"), orderId));
            }
            if (cityDestination != null && !cityDestination.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.<String>get("cityDestination")), cityDestination.trim().toLowerCase(java.util.Locale.ROOT)));
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
        return jpa.findAll(spec, pageable).map(mapper::toDto);
    }
}
