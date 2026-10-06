package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.order;

import com.essenza.draco.modules.sales.application.output.repository.OrderRepository;
import com.essenza.draco.modules.sales.application.dto.order.CreateOrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.UpdateOrderDto;
import com.essenza.draco.modules.sales.infrastructure.outbound.mappers.OrderMapper;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.OrderEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    private final JpaOrderRepository jpa;
    private final OrderMapper mapper;

    public OrderRepositoryAdapter(JpaOrderRepository jpa, OrderMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }


    @Override
    public OrderDto create(CreateOrderDto order) {
        OrderEntity saved = jpa.save(mapper.toEntity(order));
        return mapper.toDto(saved);
    }

    @Override
    public OrderDto update(Long id, UpdateOrderDto input) {
        OrderEntity saved = jpa.save(mapper.toEntity(input));
        return mapper.toDto(saved);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }

    @Override
    public Optional<OrderDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<OrderDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }

    /**
     * Búsqueda paginada: {@code query} busca en complementaryOrder
     * y, si es numérico, también por id.
     */
    public org.springframework.data.domain.Page<OrderDto> search(String query, String state,
                                                               org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.OrderEntity> spec = (root, criteria, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (query != null && !query.isBlank()) {
                String like = "%" + query.trim().toLowerCase(java.util.Locale.ROOT) + "%";
                java.util.List<jakarta.persistence.criteria.Predicate> textual = new java.util.ArrayList<>(java.util.List.of(
                        cb.like(cb.lower(cb.coalesce(root.<String>get("complementaryOrder"), "")), like)));
                if (query.trim().matches("\\d{1,18}")) {
                    Long number = Long.valueOf(query.trim());
                    textual.add(cb.equal(root.get("id"), number));
                }
                predicates.add(cb.or(textual.toArray(jakarta.persistence.criteria.Predicate[]::new)));
            }
            if (state != null && !state.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.<String>get("state")), state.trim().toLowerCase(java.util.Locale.ROOT)));
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
        return jpa.findAll(spec, pageable).map(mapper::toDto);
    }

    /** ¿La orden tiene despachos o devoluciones? (impide borrarla). */
    public boolean hasDependents(Long id) {
        // Consultas por nombre de entidad: ventas no importa clases de devolution ni shipping_logistics.
        Long dispatches = entityManager.createQuery(
                        "select count(d) from DispatchProductEntity d where d.orderId = :id", Long.class)
                .setParameter("id", id).getSingleResult();
        if (dispatches > 0) return true;
        Long devolutions = entityManager.createQuery(
                        "select count(o) from OrderDevolutionEntity o where o.orderId = :id", Long.class)
                .setParameter("id", id).getSingleResult();
        return devolutions > 0;
    }

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;
}
