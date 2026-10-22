package com.essenza.draco.modules.sales.application.services.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderEventDto;
import com.essenza.draco.modules.sales.application.output.repository.OrderEventLog;
import com.essenza.draco.modules.sales.application.output.repository.OrderInsights;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger.LinePrice;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger.LineState;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger.OrderDetails;
import com.essenza.draco.modules.sales.application.output.repository.OrderReferences;
import com.essenza.draco.modules.sales.application.output.repository.OrderRepository;
import com.essenza.draco.modules.sales.domain.model.LineTax;
import com.essenza.draco.modules.sales.domain.model.OrderStatus;
import com.essenza.draco.modules.sales.domain.model.order.CouponRule;
import com.essenza.draco.modules.sales.domain.model.order.DiscountType;
import com.essenza.draco.modules.sales.domain.model.order.OrderEventType;
import com.essenza.draco.modules.sales.domain.model.order.OrderPricing;
import com.essenza.draco.modules.sales.domain.model.order.OrderPricing.PricedLine;
import com.essenza.draco.shared.common.promotions.Coupons;
import com.essenza.draco.shared.common.promotions.Coupons.CouponTerms;
import com.essenza.draco.shared.exceptions.NotFoundException;

/**
 * Apoyo común de las órdenes empresariales (V16): lectura enriquecida, línea de tiempo,
 * cupones y precio con el descuento global repartido entre las líneas.
 */
@Component
public class OrderSoporte {

    static final ZoneId ZONE = ZoneId.of("America/Bogota");

    private final OrderLedger ledger;
    private final OrderRepository orders;
    private final OrderInsights insights;
    private final OrderEventLog events;
    private final OrderReferences references;
    private final Coupons coupons;

    public OrderSoporte(OrderLedger ledger, OrderRepository orders, OrderInsights insights, OrderEventLog events,
                        OrderReferences references, Coupons coupons) {
        this.ledger = ledger;
        this.orders = orders;
        this.insights = insights;
        this.events = events;
        this.references = references;
        this.coupons = coupons;
    }

    public OrderDetails details(Long orderId) {
        return ledger.details(orderId).orElseThrow(() -> notFound(orderId));
    }

    /** Orden con nombres, cobro, preparación y estados siguientes. */
    public OrderDto view(Long orderId) {
        return find(orderId).orElseThrow(() -> notFound(orderId));
    }

    public Optional<OrderDto> find(Long orderId) {
        return orders.findById(orderId).map(dto -> withNextStates(insights.enrich(List.of(dto)).get(0)));
    }

    public void log(Long orderId, OrderEventType type, String from, String to, String message, String actor) {
        events.append(orderId, type, from, to, clip(message, 1000), clip(actor, 120));
    }

    public List<OrderEventDto> timeline(Long orderId) {
        return events.list(orderId).stream()
                .map(e -> new OrderEventDto(e.id(), e.type().name(), e.fromState(), e.toState(), e.message(), e.actor(),
                        e.createdAt()))
                .toList();
    }

    public Optional<CouponTerms> couponByCode(String code) {
        return code == null || code.isBlank() ? Optional.empty() : coupons.findByCode(code.trim());
    }

    public void redeemCoupon(Long couponId, int delta) {
        if (couponId != null) {
            coupons.redeem(couponId, delta);
        }
    }

    /**
     * Reparte el descuento global entre las líneas, recalcula el impuesto de cada una y deja
     * el total de la orden = líneas + envío − descuento del envío.
     */
    public void reprice(Long orderId) {
        OrderDetails order = details(orderId);
        List<LineState> lines = ledger.lineStates(orderId);
        CouponRule coupon = null;
        Map<Long, Long> categories = Map.of();
        if (order.discountType() == DiscountType.COUPON) {
            CouponTerms terms = (order.couponId() != null ? coupons.findById(order.couponId()) : couponByCode(order.couponCode()))
                    .orElse(null);
            coupon = terms == null ? null : toRule(terms);
            if (coupon != null && (!coupon.categories().isEmpty() || !coupon.excludedCategories().isEmpty())) {
                categories = references.categories(lines.stream().map(LineState::productId).toList());
            }
        }
        Map<Long, Long> categoryOf = categories;
        List<PricedLine> priced = lines.stream()
                .map(line -> new PricedLine(line.id(), line.productId(), categoryOf.get(line.productId()),
                        zero(line.subtotal()).subtract(zero(line.discount()))))
                .toList();
        boolean redeemed = OrderStatus.tryParse(order.state()).map(OrderStatus::stockCommitted).orElse(false);
        OrderPricing.Result result = OrderPricing.price(priced, order.discountType(), order.discountValue(), coupon, redeemed,
                order.delivery().shippingAmount(), LocalDateTime.now(ZONE));
        Map<Long, LinePrice> prices = new LinkedHashMap<>();
        for (LineState line : lines) {
            BigDecimal share = result.lineDiscounts().getOrDefault(line.id(), BigDecimal.ZERO);
            BigDecimal total = zero(line.subtotal()).subtract(zero(line.discount())).subtract(share)
                    .setScale(2, RoundingMode.HALF_UP);
            LineTax tax = line.tax() == null ? null : line.tax().forTotal(total);
            prices.put(line.id(), new LinePrice(share.setScale(2, RoundingMode.HALF_UP), total, tax));
        }
        ledger.applyPricing(orderId, prices, result.discountAmount(), result.shippingDiscount());
        ledger.refreshTotal(orderId);
    }

    static CouponRule toRule(CouponTerms t) {
        return new CouponRule(t.id(), t.code(), t.discountType(), zero(t.discountValue()), t.maximumDiscount(), t.minimumOrder(),
                t.validFrom(), t.validUntil(), t.active(), t.usageLimit(), t.usageCount(), t.products(), t.excludedProducts(),
                t.categories(), t.excludedCategories());
    }

    static OrderDto withNextStates(OrderDto dto) {
        dto.setNextStates(OrderStatus.tryParse(dto.getState())
                .map(status -> status.next().stream().map(Enum::name).toList())
                .orElseGet(() -> java.util.Arrays.stream(OrderStatus.values()).map(Enum::name).toList()));
        return dto;
    }

    static NotFoundException notFound(Long orderId) {
        return new NotFoundException("No se encontró la orden " + orderId + ".");
    }

    static BigDecimal zero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2) : value;
    }

    static String clip(String value, int max) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String text = value.strip();
        return text.length() > max ? text.substring(0, max) : text;
    }

    static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
