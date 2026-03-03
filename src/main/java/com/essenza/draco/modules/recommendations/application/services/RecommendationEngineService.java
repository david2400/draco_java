//package com.essenza.draco.modules.recommendations.application.services;
//
//import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.ProductRecommendationDto;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class RecommendationEngineService {
//
//    private final CustomerBehaviorService customerBehaviorService;
//    private final ProductSimilarityService productSimilarityService;
//    private final TrendingProductsService trendingProductsService;
//
//    public RecommendationEngineService(CustomerBehaviorService customerBehaviorService,
//                                     ProductSimilarityService productSimilarityService,
//                                     TrendingProductsService trendingProductsService) {
//        this.customerBehaviorService = customerBehaviorService;
//        this.productSimilarityService = productSimilarityService;
//        this.trendingProductsService = trendingProductsService;
//    }
//
//    /**
//     * Genera recomendaciones personalizadas para un cliente
//     */
//    public List<ProductRecommendationDto> generatePersonalizedRecommendations(Long customerId, String context, int limit) {
//        List<ProductRecommendationDto> recommendations = new ArrayList<>();
//
//        // 1. Recomendaciones colaborativas (40% del peso)
//        List<ProductRecommendationDto> collaborative = generateCollaborativeRecommendations(customerId, limit);
//        recommendations.addAll(weightRecommendations(collaborative, 0.4, "COLLABORATIVE"));
//
//        // 2. Recomendaciones basadas en contenido (30% del peso)
//        List<ProductRecommendationDto> contentBased = generateContentBasedRecommendations(customerId, limit);
//        recommendations.addAll(weightRecommendations(contentBased, 0.3, "CONTENT_BASED"));
//
//        // 3. Productos en tendencia (20% del peso)
//        List<ProductRecommendationDto> trending = generateTrendingRecommendations(customerId, limit);
//        recommendations.addAll(weightRecommendations(trending, 0.2, "TRENDING"));
//
//        // 4. Venta cruzada basada en carrito actual (10% del peso)
//        List<ProductRecommendationDto> crossSell = generateCrossSellRecommendations(customerId, context, limit);
//        recommendations.addAll(weightRecommendations(crossSell, 0.1, "CROSS_SELL"));
//
//        // Combinar y ordenar por puntuación
//        return recommendations.stream()
//                .collect(Collectors.groupingBy(ProductRecommendationDto::getProductId))
//                .values().stream()
//                .map(this::combineRecommendationScores)
//                .sorted((r1, r2) -> r2.getScore().compareTo(r1.getScore()))
//                .limit(limit)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Filtrado colaborativo - "Usuarios que compraron esto también compraron"
//     */
//    private List<ProductRecommendationDto> generateCollaborativeRecommendations(Long customerId, int limit) {
//        // Obtener historial de compras del cliente
//        List<Long> customerPurchases = customerBehaviorService.getCustomerPurchaseHistory(customerId);
//
//        // Encontrar clientes similares
//        List<Long> similarCustomers = customerBehaviorService.findSimilarCustomers(customerId, customerPurchases);
//
//        // Obtener productos comprados por clientes similares
//        Map<Long, Integer> productFrequency = new HashMap<>();
//        for (Long similarCustomer : similarCustomers) {
//            List<Long> similarCustomerPurchases = customerBehaviorService.getCustomerPurchaseHistory(similarCustomer);
//            for (Long productId : similarCustomerPurchases) {
//                if (!customerPurchases.contains(productId)) {
//                    productFrequency.merge(productId, 1, Integer::sum);
//                }
//            }
//        }
//
//        // Convertir a recomendaciones con puntuación
//        return productFrequency.entrySet().stream()
//                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
//                .limit(limit)
//                .map(entry -> createRecommendation(customerId, entry.getKey(),
//                    calculateCollaborativeScore(entry.getValue(), similarCustomers.size()),
//                    "Clientes similares también compraron este producto"))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Recomendaciones basadas en contenido - productos similares a los que le gustan
//     */
//    private List<ProductRecommendationDto> generateContentBasedRecommendations(Long customerId, int limit) {
//        // Obtener productos favoritos del cliente (alta calificación, compras frecuentes)
//        List<Long> favoriteProducts = customerBehaviorService.getCustomerFavoriteProducts(customerId);
//
//        List<ProductRecommendationDto> recommendations = new ArrayList<>();
//
//        for (Long favoriteProductId : favoriteProducts) {
//            // Encontrar productos similares
//            List<Long> similarProducts = productSimilarityService.findSimilarProducts(favoriteProductId, limit);
//
//            for (Long similarProductId : similarProducts) {
//                BigDecimal similarity = productSimilarityService.calculateSimilarity(favoriteProductId, similarProductId);
//                recommendations.add(createRecommendation(customerId, similarProductId, similarity,
//                    "Similar a productos que te gustaron"));
//            }
//        }
//
//        return recommendations.stream()
//                .collect(Collectors.groupingBy(ProductRecommendationDto::getProductId))
//                .values().stream()
//                .map(this::combineRecommendationScores)
//                .sorted((r1, r2) -> r2.getScore().compareTo(r1.getScore()))
//                .limit(limit)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Productos en tendencia
//     */
//    private List<ProductRecommendationDto> generateTrendingRecommendations(Long customerId, int limit) {
//        List<Long> trendingProducts = trendingProductsService.getTrendingProducts(limit);
//
//        return trendingProducts.stream()
//                .map(productId -> {
//                    BigDecimal trendScore = trendingProductsService.getTrendScore(productId);
//                    return createRecommendation(customerId, productId, trendScore,
//                        "Producto en tendencia");
//                })
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Venta cruzada basada en productos en el carrito
//     */
//    private List<ProductRecommendationDto> generateCrossSellRecommendations(Long customerId, String context, int limit) {
//        if (!"CART".equals(context) && !"CHECKOUT".equals(context)) {
//            return new ArrayList<>();
//        }
//
//        // Obtener productos en el carrito actual
//        List<Long> cartProducts = customerBehaviorService.getCurrentCartProducts(customerId);
//
//        List<ProductRecommendationDto> recommendations = new ArrayList<>();
//
//        for (Long cartProductId : cartProducts) {
//            // Encontrar productos frecuentemente comprados juntos
//            List<Long> frequentlyBoughtTogether = productSimilarityService.getFrequentlyBoughtTogether(cartProductId);
//
//            for (Long complementaryProductId : frequentlyBoughtTogether) {
//                if (!cartProducts.contains(complementaryProductId)) {
//                    BigDecimal crossSellScore = productSimilarityService.getCrossSellScore(cartProductId, complementaryProductId);
//                    recommendations.add(createRecommendation(customerId, complementaryProductId, crossSellScore,
//                        "Frecuentemente comprado junto con productos en tu carrito"));
//                }
//            }
//        }
//
//        return recommendations.stream()
//                .limit(limit)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Aplica peso a las recomendaciones según el algoritmo
//     */
//    private List<ProductRecommendationDto> weightRecommendations(List<ProductRecommendationDto> recommendations,
//                                                               double weight, String type) {
//        return recommendations.stream()
//                .map(rec -> {
//                    rec.setScore(rec.getScore().multiply(BigDecimal.valueOf(weight)));
//                    rec.setRecommendationType(type);
//                    return rec;
//                })
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Combina puntuaciones de múltiples algoritmos para el mismo producto
//     */
//    private ProductRecommendationDto combineRecommendationScores(List<ProductRecommendationDto> recommendations) {
//        ProductRecommendationDto combined = recommendations.get(0);
//
//        BigDecimal totalScore = recommendations.stream()
//                .map(ProductRecommendationDto::getScore)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        combined.setScore(totalScore);
//        combined.setRecommendationType("HYBRID");
//
//        // Combinar razones
//        String combinedReason = recommendations.stream()
//                .map(ProductRecommendationDto::getReason)
//                .distinct()
//                .collect(Collectors.joining("; "));
//        combined.setReason(combinedReason);
//
//        return combined;
//    }
//
//    /**
//     * Crea una recomendación básica
//     */
//    private ProductRecommendationDto createRecommendation(Long customerId, Long productId,
//                                                        BigDecimal score, String reason) {
//        return ProductRecommendationDto.builder()
//                .customerId(customerId)
//                .productId(productId)
//                .score(score.setScale(4, RoundingMode.HALF_UP))
//                .reason(reason)
//                .isClicked(false)
//                .isPurchased(false)
//                .build();
//    }
//
//    /**
//     * Calcula puntuación para filtrado colaborativo
//     */
//    private BigDecimal calculateCollaborativeScore(int frequency, int totalSimilarCustomers) {
//        if (totalSimilarCustomers == 0) return BigDecimal.ZERO;
//
//        double score = (double) frequency / totalSimilarCustomers;
//        return BigDecimal.valueOf(score).setScale(4, RoundingMode.HALF_UP);
//    }
//
//    /**
//     * Registra interacción con recomendación (click, compra)
//     */
//    public void recordRecommendationInteraction(Long recommendationId, String interactionType) {
//        // Implementar registro de interacciones para mejorar el algoritmo
//        switch (interactionType.toLowerCase()) {
//            case "click":
//                // Actualizar isClicked = true
//                break;
//            case "purchase":
//                // Actualizar isPurchased = true
//                break;
//            case "dismiss":
//                // Registrar que el usuario descartó la recomendación
//                break;
//        }
//    }
//}
