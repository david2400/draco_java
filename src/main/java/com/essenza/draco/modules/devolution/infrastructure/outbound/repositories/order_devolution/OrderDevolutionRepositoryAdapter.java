package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.order_devolution;

import com.essenza.draco.modules.devolution.application.output.repository.OrderDevolutionRepository;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.CreateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.UpdateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.mappers.OrderDevolutionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderDevolutionRepositoryAdapter implements OrderDevolutionRepository {

    private final JpaOrderDevolutionRepository jpa;
    private final OrderDevolutionMapper mapper;

    public OrderDevolutionRepositoryAdapter(JpaOrderDevolutionRepository jpa, OrderDevolutionMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public OrderDevolutionDto create(CreateOrderDevolutionDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public OrderDevolutionDto update(Long id, UpdateOrderDevolutionDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OrderDevolution not found: " + id));
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
    public Optional<OrderDevolutionDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<OrderDevolutionDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }

    /**
     * Búsqueda paginada: {@code query} busca en externalReference, observation
     * y, si es numérico, también por id / orderId.
     */
    public org.springframework.data.domain.Page<OrderDevolutionDto> search(String query, String state, Long motiveDevolutionId, Long orderId,
                                                               org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.OrderDevolutionEntity> spec = (root, criteria, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (query != null && !query.isBlank()) {
                String like = "%" + query.trim().toLowerCase(java.util.Locale.ROOT) + "%";
                java.util.List<jakarta.persistence.criteria.Predicate> textual = new java.util.ArrayList<>(java.util.List.of(
                        cb.like(cb.lower(cb.coalesce(root.<String>get("externalReference"), "")), like),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("observation"), "")), like)));
                if (query.trim().matches("\\d{1,18}")) {
                    Long number = Long.valueOf(query.trim());
                    textual.add(cb.equal(root.get("id"), number));
                    textual.add(cb.equal(root.get("orderId"), number));
                }
                predicates.add(cb.or(textual.toArray(jakarta.persistence.criteria.Predicate[]::new)));
            }
            if (state != null && !state.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.<String>get("state")), state.trim().toLowerCase(java.util.Locale.ROOT)));
            }
            if (motiveDevolutionId != null) {
                predicates.add(cb.equal(root.get("motiveDevolutionId"), motiveDevolutionId));
            }
            if (orderId != null) {
                predicates.add(cb.equal(root.get("orderId"), orderId));
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
        return jpa.findAll(spec, pageable).map(mapper::toDto);
    }
}
