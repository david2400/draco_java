package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.sales.application.output.repository.OrderLedger;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.OrderEntity;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.ProductOrderEntity;
import com.essenza.draco.modules.sales.infrastructure.outbound.repositories.product_order.JpaProductOrderRepository;
import com.essenza.draco.shared.exceptions.NotFoundException;

import jakarta.persistence.EntityManager;

@Repository
public class OrderLedgerAdapter implements OrderLedger {

    private final JpaOrderRepository orders;
    private final JpaProductOrderRepository lines;
    private final EntityManager em;

    public OrderLedgerAdapter(JpaOrderRepository orders, JpaProductOrderRepository lines, EntityManager em) {
        this.orders = orders;
        this.lines = lines;
        this.em = em;
    }

    @Override
    public Optional<OrderHeader> lockOrder(Long orderId) {
        return orders.findForUpdate(orderId).map(order -> new OrderHeader(order.getId(), order.getState(),
                Boolean.TRUE.equals(order.getStockManaged()), order.getCreatedAt()));
    }

    @Override
    public Long createOrder(String complementaryOrder) {
        OrderEntity order = new OrderEntity();
        order.setComplementaryOrder(complementaryOrder == null || complementaryOrder.isBlank() ? null : complementaryOrder.trim());
        order.setState("PENDING");
        order.setTotal(BigDecimal.ZERO.setScale(2));
        order.setStockManaged(true);
        return orders.saveAndFlush(order).getId();
    }

    @Override
    public void updateComplementary(Long orderId, String complementaryOrder) {
        OrderEntity order = order(orderId);
        order.setComplementaryOrder(complementaryOrder == null || complementaryOrder.isBlank() ? null : complementaryOrder.trim());
        orders.save(order);
    }

    @Override
    public void setState(Long orderId, String state, String cancelReason) {
        OrderEntity order = order(orderId);
        order.setState(state);
        order.setCancelReason(cancelReason);
        orders.saveAndFlush(order);
    }

    @Override
    public void refreshTotal(Long orderId) {
        lines.flush();
        Object sum = em.createNativeQuery(
                        "SELECT COALESCE(SUM(total), 0) FROM product_orders WHERE order_id = :id AND deleted = 0")
                .setParameter("id", orderId).getSingleResult();
        OrderEntity order = order(orderId);
        order.setTotal(new BigDecimal(sum.toString()).setScale(2, java.math.RoundingMode.HALF_UP));
        orders.saveAndFlush(order);
    }

    @Override
    public Optional<OrderLine> findLine(Long lineId) {
        return lines.findById(lineId).map(OrderLedgerAdapter::toLine);
    }

    @Override
    public List<OrderLine> lines(Long orderId) {
        return lines.findByOrderIdIn(List.of(orderId)).stream().map(OrderLedgerAdapter::toLine).toList();
    }

    @Override
    public OrderLine saveLine(OrderLine line) {
        ProductOrderEntity entity = line.id() == null ? new ProductOrderEntity()
                : lines.findById(line.id()).orElseThrow(() -> new NotFoundException("No se encontró la línea " + line.id() + "."));
        entity.setOrderId(line.orderId());
        entity.setProductId(line.productId());
        entity.setSkuId(line.skuId());
        entity.setSkuCode(line.skuCode());
        entity.setProductName(line.productName());
        entity.setUnitPrice(line.unitPrice());
        entity.setQuantity(line.quantity());
        entity.setDiscount(line.discount());
        entity.setSubtotal(line.subtotal());
        entity.setTotal(line.total());
        return toLine(lines.saveAndFlush(entity));
    }

    @Override
    public void deleteLine(Long lineId) {
        lines.findById(lineId).ifPresent(entity -> {
            lines.delete(entity);
            lines.flush();
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Long> expiredPendingOrders(Instant createdBefore) {
        List<Number> ids = em.createNativeQuery("""
                SELECT id_order FROM orders
                 WHERE stock_managed = 1 AND state = 'PENDING' AND deleted = 0 AND created_at < :cutoff
                 ORDER BY id_order
                """).setParameter("cutoff", Timestamp.from(createdBefore)).getResultList();
        return ids.stream().map(Number::longValue).toList();
    }

    private OrderEntity order(Long orderId) {
        return orders.findById(orderId).orElseThrow(() -> new NotFoundException("No se encontró la orden " + orderId + "."));
    }

    static OrderLine toLine(ProductOrderEntity entity) {
        return new OrderLine(entity.getId(), entity.getOrderId(), entity.getProductId(), entity.getSkuId(),
                entity.getSkuCode(), entity.getProductName(), entity.getUnitPrice(),
                entity.getQuantity() == null ? 0 : entity.getQuantity(), entity.getDiscount(), entity.getSubtotal(),
                entity.getTotal());
    }
}
