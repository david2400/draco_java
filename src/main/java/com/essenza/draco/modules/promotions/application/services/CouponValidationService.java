package com.essenza.draco.modules.promotions.application.services;

import com.essenza.draco.modules.promotions.application.input.coupon.ValidateCouponUseCase;
import com.essenza.draco.modules.promotions.application.output.repository.CouponRepository;
import com.essenza.draco.modules.promotions.domain.dto.coupon.CouponDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CouponValidationService implements ValidateCouponUseCase {

    private final CouponRepository couponRepository;

    public CouponValidationService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public Optional<CouponDto> validateCoupon(String couponCode, BigDecimal orderAmount, 
                                            List<Long> productIds, List<Long> categoryIds) {
        
        // Buscar cupón por código
        Optional<CouponDto> couponOpt = couponRepository.findByCode(couponCode);
        if (couponOpt.isEmpty()) {
            return Optional.empty();
        }

        CouponDto coupon = couponOpt.get();

        // Validar si está activo
        if (!coupon.getIsActive()) {
            return Optional.empty();
        }

        // Validar fechas de vigencia
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidUntil())) {
            return Optional.empty();
        }

        // Validar límite de uso
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            return Optional.empty();
        }

        // Validar monto mínimo de orden
        if (coupon.getMinimumOrderAmount() != null && 
            orderAmount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            return Optional.empty();
        }

        // Validar productos aplicables
        if (!isApplicableToProducts(coupon, productIds)) {
            return Optional.empty();
        }

        // Validar categorías aplicables
        if (!isApplicableToCategories(coupon, categoryIds)) {
            return Optional.empty();
        }

        return Optional.of(coupon);
    }

    private boolean isApplicableToProducts(CouponDto coupon, List<Long> productIds) {
        // Si no hay restricciones de productos, es aplicable
        if (coupon.getApplicableProducts() == null && coupon.getExcludedProducts() == null) {
            return true;
        }

        // Verificar productos excluidos
        if (coupon.getExcludedProducts() != null) {
            List<String> excludedProducts = Arrays.asList(coupon.getExcludedProducts().split(","));
            for (Long productId : productIds) {
                if (excludedProducts.contains(productId.toString())) {
                    return false;
                }
            }
        }

        // Verificar productos aplicables
        if (coupon.getApplicableProducts() != null) {
            List<String> applicableProducts = Arrays.asList(coupon.getApplicableProducts().split(","));
            for (Long productId : productIds) {
                if (applicableProducts.contains(productId.toString())) {
                    return true;
                }
            }
            return false; // No se encontró ningún producto aplicable
        }

        return true;
    }

    private boolean isApplicableToCategories(CouponDto coupon, List<Long> categoryIds) {
        // Si no hay restricciones de categorías, es aplicable
        if (coupon.getApplicableCategories() == null && coupon.getExcludedCategories() == null) {
            return true;
        }

        // Verificar categorías excluidas
        if (coupon.getExcludedCategories() != null) {
            List<String> excludedCategories = Arrays.asList(coupon.getExcludedCategories().split(","));
            for (Long categoryId : categoryIds) {
                if (excludedCategories.contains(categoryId.toString())) {
                    return false;
                }
            }
        }

        // Verificar categorías aplicables
        if (coupon.getApplicableCategories() != null) {
            List<String> applicableCategories = Arrays.asList(coupon.getApplicableCategories().split(","));
            for (Long categoryId : categoryIds) {
                if (applicableCategories.contains(categoryId.toString())) {
                    return true;
                }
            }
            return false; // No se encontró ninguna categoría aplicable
        }

        return true;
    }

    /**
     * Calcula el descuento aplicable basado en el cupón y el monto de la orden
     */
    public BigDecimal calculateDiscount(CouponDto coupon, BigDecimal orderAmount) {
        BigDecimal discount = BigDecimal.ZERO;

        switch (coupon.getDiscountType()) {
            case "PERCENTAGE":
                discount = orderAmount.multiply(coupon.getDiscountValue().divide(BigDecimal.valueOf(100)));
                break;
            case "FIXED_AMOUNT":
                discount = coupon.getDiscountValue();
                break;
            case "FREE_SHIPPING":
                // El descuento de envío gratis se maneja en el módulo de shipping
                discount = BigDecimal.ZERO;
                break;
        }

        // Aplicar límite máximo de descuento si existe
        if (coupon.getMaximumDiscountAmount() != null && 
            discount.compareTo(coupon.getMaximumDiscountAmount()) > 0) {
            discount = coupon.getMaximumDiscountAmount();
        }

        // El descuento no puede ser mayor al monto de la orden
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }

        return discount;
    }
}
