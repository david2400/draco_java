package com.essenza.draco.modules.sales.application.services.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentActionDto;
import com.essenza.draco.modules.sales.application.input.order.FindOrderByIdUseCase;
import com.essenza.draco.modules.sales.application.input.payment.RefundPaymentUseCase;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger;
import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;
import com.essenza.draco.modules.sales.application.output.repository.PaymentRepository;
import com.essenza.draco.modules.sales.domain.model.payment.Payment;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;
import com.essenza.draco.shared.common.sales.OrderReturns;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

/**
 * Lado de ventas de las devoluciones: líneas que se pueden devolver y reembolso sobre los pagos
 * de la orden (del más reciente al más antiguo), acreditando el valor devuelto a la orden.
 */
@Service
@Transactional
public class OrderReturnsService implements OrderReturns {

    private final OrderLedger ledger;
    private final FindOrderByIdUseCase findOrder;
    private final PaymentRepository payments;
    private final PaymentMethodRepository methods;
    private final RefundPaymentUseCase refundPayment;

    public OrderReturnsService(OrderLedger ledger, FindOrderByIdUseCase findOrder, PaymentRepository payments,
                               PaymentMethodRepository methods, RefundPaymentUseCase refundPayment) {
        this.ledger = ledger;
        this.findOrder = findOrder;
        this.payments = payments;
        this.methods = methods;
        this.refundPayment = refundPayment;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReturnableOrder> find(Long orderId) {
        return findOrder.findById(orderId).map(order -> new ReturnableOrder(order.getId(), order.getState(),
                money(order.getTotal()), money(order.getReturnedAmount()),
                ledger.lines(orderId).stream()
                        .sorted(Comparator.comparing(OrderLedger.OrderLine::id))
                        .map(line -> new ReturnableLine(line.id(), line.productId(), line.skuId(), line.skuCode(),
                                line.productName(), line.quantity(), unitPrice(line), money(line.total())))
                        .toList()));
    }

    @Override
    public RefundResult refund(Long orderId, BigDecimal creditValue, BigDecimal amount, String reason) {
        ledger.lockOrder(orderId).orElseThrow(() -> new NotFoundException("Orden no encontrada: " + orderId));
        BigDecimal credit = money(creditValue);
        BigDecimal requested = money(amount);
        if (credit.signum() < 0 || requested.signum() < 0) {
            throw new IllegalArgumentException("Los montos de la devolución no pueden ser negativos.");
        }
        List<Payment> refundable = refundablePayments(orderId);
        BigDecimal available = refundable.stream().map(Payment::refundable).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (requested.compareTo(available) > 0) {
            throw new ConflictException("Los pagos de la orden " + orderId + " solo permiten reembolsar "
                    + available.toPlainString() + ". Registra el resto como saldo a favor (monto menor).");
        }
        List<RefundedPayment> done = new ArrayList<>();
        BigDecimal remaining = requested;
        for (Payment payment : refundable) {
            if (remaining.signum() <= 0) {
                break;
            }
            BigDecimal take = remaining.min(payment.refundable());
            refundPayment.refund(payment.getId(), new PaymentActionDto(reason, null, take));
            String method = methods.findById(payment.getMethodId()).map(PaymentMethod::name).orElse(null);
            done.add(new RefundedPayment(payment.getId(), payment.getPaymentReference(), method, take));
            remaining = remaining.subtract(take);
        }
        if (credit.signum() > 0) {
            ledger.addReturnedAmount(orderId, credit);
        }
        return new RefundResult(requested, done);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal refundable(Long orderId) {
        return refundablePayments(orderId).stream().map(Payment::refundable).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Pagos con saldo reembolsable, el más reciente primero. */
    private List<Payment> refundablePayments(Long orderId) {
        return payments.findByOrderId(orderId).stream()
                .filter(payment -> payment.refundable().signum() > 0)
                .sorted(Comparator.comparing((Payment p) -> p.getApprovedAt() == null ? Instant.EPOCH : p.getApprovedAt())
                        .thenComparing(Payment::getId).reversed())
                .toList();
    }

    private static BigDecimal unitPrice(OrderLedger.OrderLine line) {
        if (line.quantity() <= 0 || line.total() == null) {
            return money(line.unitPrice());
        }
        return line.total().divide(BigDecimal.valueOf(line.quantity()), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal money(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2) : value.setScale(2, RoundingMode.HALF_UP);
    }
}
