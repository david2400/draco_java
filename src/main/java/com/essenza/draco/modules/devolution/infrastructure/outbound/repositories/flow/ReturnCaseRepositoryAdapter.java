package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import com.essenza.draco.modules.devolution.application.output.repository.ReturnCaseRepository;
import com.essenza.draco.modules.devolution.domain.model.DevolutionState;
import com.essenza.draco.modules.devolution.domain.model.ItemCondition;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;
import com.essenza.draco.modules.devolution.domain.model.ReturnItem;
import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.OrderDevolutionDetailEntity;
import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.OrderDevolutionEntity;
import com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.order_devolution.JpaOrderDevolutionRepository;
import com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.order_devolution_detail.JpaOrderDevolutionDetailRepository;

@Repository
public class ReturnCaseRepositoryAdapter implements ReturnCaseRepository {

    private final JpaOrderDevolutionRepository devolutions;
    private final JpaOrderDevolutionDetailRepository details;
    private final EntityManager em;

    public ReturnCaseRepositoryAdapter(JpaOrderDevolutionRepository devolutions, JpaOrderDevolutionDetailRepository details,
                                       EntityManager em) {
        this.devolutions = devolutions;
        this.details = details;
        this.em = em;
    }

    @Override
    public Optional<ReturnCase> find(Long id) {
        return devolutions.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ReturnCase> lock(Long id) {
        return devolutions.findForUpdate(id).map(this::toDomain);
    }

    @Override
    public ReturnCase save(ReturnCase c) {
        OrderDevolutionEntity e = c.id() == null ? new OrderDevolutionEntity()
                : devolutions.findById(c.id()).orElseThrow(() -> new IllegalStateException("Devolución no encontrada: " + c.id()));
        e.setOrderId(c.orderId());
        e.setState(c.state().code());
        e.setMotiveDevolutionId(c.motiveId());
        e.setObservation(c.observation());
        e.setReturnMethodId(c.returnMethodId());
        e.setRefundMethodId(c.refundMethodId());
        e.setExternalReference(c.externalReference());
        e.setWarehouseId(c.warehouseId());
        e.setTotalRefundAmount(c.creditValue());
        e.setRefundedAmount(c.refundedAmount());
        e.setApprovedBy(c.approvedBy());
        e.setApprovedAt(c.approvedAt());
        e.setReceivedBy(c.receivedBy());
        e.setReceivedAt(c.receivedAt());
        e.setInspectedBy(c.inspectedBy());
        e.setInspectedAt(c.inspectedAt());
        e.setInspectionNotes(c.inspectionNotes());
        e.setRejectedReason(c.rejectedReason());
        e.setRejectedBy(c.rejectedBy());
        e.setRejectedAt(c.rejectedAt());
        e.setRefundedBy(c.refundedBy());
        e.setRefundedAt(c.refundedAt());
        e.setRefundDetail(c.refundDetail());
        OrderDevolutionEntity saved = devolutions.saveAndFlush(e);
        for (ReturnItem item : c.items()) {
            OrderDevolutionDetailEntity d = item.id() == null ? new OrderDevolutionDetailEntity()
                    : details.findById(item.id()).orElseThrow(() -> new IllegalStateException("Línea no encontrada: " + item.id()));
            d.setOrderDevolutionId(saved.getId());
            d.setProductOrderId(item.productOrderId());
            d.setProductId(item.productId());
            d.setSkuId(item.skuId());
            d.setSkuCode(item.skuCode());
            d.setProductName(item.productName());
            d.setQuantity(item.quantity());
            d.setUnitPrice(item.unitPrice());
            d.setObservation(item.observation() == null || item.observation().isBlank() ? "-" : item.observation());
            d.setReceivedQuantity(item.receivedQuantity());
            d.setOrderCondition(item.condition() == null ? null : item.condition().name());
            d.setRestockingFee(item.restockingFee());
            d.setRefundAmount(item.refundAmount());
            d.setRestock(item.restock());
            d.setRestockedQuantity(item.restockedQuantity());
            details.save(d);
        }
        details.flush();
        return toDomain(devolutions.findById(saved.getId()).orElseThrow());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Integer> returnedUnits(Long orderId, Long excludeDevolutionId) {
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT d.product_order_id, SUM(COALESCE(d.received_quantity, d.quantity))
                          FROM order_devolution_details d
                          JOIN order_devolutions o ON o.id_order_devolution = d.order_devolution_id
                         WHERE o.order_id = :orderId AND o.deleted = 0 AND d.deleted = 0
                           AND UPPER(o.state) <> 'X' AND o.id_order_devolution <> :exclude
                         GROUP BY d.product_order_id""")
                .setParameter("orderId", orderId)
                .setParameter("exclude", excludeDevolutionId == null ? -1L : excludeDevolutionId)
                .getResultList();
        Map<Long, Integer> units = new HashMap<>();
        for (Object[] row : rows) {
            units.put(((Number) row[0]).longValue(), ((Number) row[1]).intValue());
        }
        return units;
    }

    private ReturnCase toDomain(OrderDevolutionEntity e) {
        List<ReturnItem> items = details.findByOrderDevolutionIdOrderByIdAsc(e.getId()).stream()
                .map(ReturnCaseRepositoryAdapter::toItem).toList();
        return new ReturnCase(e.getId(), e.getOrderId(), DevolutionState.fromCode(e.getState()).orElse(DevolutionState.PENDING),
                e.getMotiveDevolutionId(), e.getObservation(), e.getReturnMethodId(), e.getRefundMethodId(),
                e.getExternalReference(), e.getWarehouseId(), e.getTotalRefundAmount(), e.getRefundedAmount(),
                e.getApprovedBy(), e.getApprovedAt(), e.getReceivedBy(), e.getReceivedAt(), e.getInspectedBy(),
                e.getInspectedAt(), e.getInspectionNotes(), e.getRejectedReason(), e.getRejectedBy(), e.getRejectedAt(),
                e.getRefundedBy(), e.getRefundedAt(), e.getRefundDetail(), items);
    }

    private static ReturnItem toItem(OrderDevolutionDetailEntity d) {
        ItemCondition condition = null;
        if (d.getOrderCondition() != null && !d.getOrderCondition().isBlank()) {
            try {
                condition = ItemCondition.valueOf(d.getOrderCondition().trim().toUpperCase(java.util.Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                // Valor heredado fuera del catálogo: se trata como sin inspeccionar.
            }
        }
        return new ReturnItem(d.getId(), d.getProductOrderId(), d.getProductId(), d.getSkuId(), d.getSkuCode(),
                d.getProductName(), d.getQuantity() == null ? 1 : Math.max(1, d.getQuantity()), d.getUnitPrice(),
                d.getObservation(), d.getReceivedQuantity(), condition, d.getRestockingFee(), d.getRefundAmount(),
                d.getRestock(), d.getRestockedQuantity() == null ? 0 : d.getRestockedQuantity());
    }
}
