//package com.essenza.draco.modules.smart_search.application.services;
//
//import com.essenza.draco.modules.smart_search.domain.dto.search_result.SearchResultDto;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class SmartSearchService {
//
//    private final SearchIndexService searchIndexService;
//    private final SearchAnalyticsService searchAnalyticsService;
//    private final AutocompleteService autocompleteService;
//
//    public SmartSearchService(SearchIndexService searchIndexService,
//                            SearchAnalyticsService searchAnalyticsService,
//                            AutocompleteService autocompleteService) {
//        this.searchIndexService = searchIndexService;
//        this.searchAnalyticsService = searchAnalyticsService;
//        this.autocompleteService = autocompleteService;
//    }
//
//    /**
//     * Búsqueda inteligente con múltiples algoritmos
//     */
//    public SearchResultDto search(String query, Map<String, List<String>> filters,
//                                String sortBy, int page, int pageSize, Long customerId) {
//
//        long startTime = System.currentTimeMillis();
//        String searchId = generateSearchId();
//
//        try {
//            // 1. Procesar y limpiar query
//            String processedQuery = preprocessQuery(query);
//
//            // 2. Detectar intención de búsqueda
//            SearchIntent intent = detectSearchIntent(processedQuery);
//
//            // 3. Expandir query con sinónimos y correcciones
//            List<String> expandedQueries = expandQuery(processedQuery);
//
//            // 4. Ejecutar búsqueda multi-algoritmo
//            SearchResultDto result = executeMultiAlgorithmSearch(expandedQueries, filters, sortBy, page, pageSize, intent);
//
//            // 5. Personalizar resultados basado en historial del cliente
//            if (customerId != null) {
//                result = personalizeResults(result, customerId);
//            }
//
//            // 6. Generar sugerencias y correcciones
//            result.setSuggestions(generateSuggestions(processedQuery));
//            result.setCorrections(generateCorrections(processedQuery));
//
//            // 7. Calcular tiempo de búsqueda
//            long searchTime = System.currentTimeMillis() - startTime;
//            result.setSearchTimeMs(searchTime);
//            result.setSearchId(searchId);
//
//            // 8. Registrar analytics
//            searchAnalyticsService.recordSearch(searchId, query, customerId, result.getTotalResults(), searchTime);
//
//            return result;
//
//        } catch (Exception e) {
//            // Fallback a búsqueda simple
//            return executeSimpleSearch(query, filters, sortBy, page, pageSize);
//        }
//    }
//
//    /**
//     * Preprocesa la query de búsqueda
//     */
//    private String preprocessQuery(String query) {
//        if (query == null) return "";
//
//        return query.trim()
//                   .toLowerCase()
//                   .replaceAll("[^a-zA-Z0-9\\s]", " ") // Remover caracteres especiales
//                   .replaceAll("\\s+", " "); // Normalizar espacios
//    }
//
//    /**
//     * Detecta la intención de búsqueda del usuario
//     */
//    private SearchIntent detectSearchIntent(String query) {
//        // Patrones para detectar intención
//        if (query.matches(".*\\b(precio|costo|barato|económico)\\b.*")) {
//            return SearchIntent.PRICE_FOCUSED;
//        }
//        if (query.matches(".*\\b(marca|brand)\\b.*")) {
//            return SearchIntent.BRAND_FOCUSED;
//        }
//        if (query.matches(".*\\b(color|talla|tamaño|size)\\b.*")) {
//            return SearchIntent.ATTRIBUTE_FOCUSED;
//        }
//        if (query.matches(".*\\b(mejor|top|recomendado|popular)\\b.*")) {
//            return SearchIntent.QUALITY_FOCUSED;
//        }
//
//        return SearchIntent.GENERAL;
//    }
//
//    /**
//     * Expande la query con sinónimos y variaciones
//     */
//    private List<String> expandQuery(String query) {
//        List<String> expandedQueries = new ArrayList<>();
//        expandedQueries.add(query);
//
//        // Diccionario de sinónimos
//        Map<String, List<String>> synonyms = Map.of(
//            "celular", Arrays.asList("móvil", "teléfono", "smartphone"),
//            "laptop", Arrays.asList("computadora", "portátil", "notebook"),
//            "zapatos", Arrays.asList("calzado", "tenis", "zapatillas"),
//            "camisa", Arrays.asList("blusa", "playera", "camiseta")
//        );
//
//        // Expandir con sinónimos
//        for (Map.Entry<String, List<String>> entry : synonyms.entrySet()) {
//            if (query.contains(entry.getKey())) {
//                for (String synonym : entry.getValue()) {
//                    expandedQueries.add(query.replace(entry.getKey(), synonym));
//                }
//            }
//        }
//
//        return expandedQueries;
//    }
//
//    /**
//     * Ejecuta búsqueda con múltiples algoritmos
//     */
//    private SearchResultDto executeMultiAlgorithmSearch(List<String> queries, Map<String, List<String>> filters,
//                                                      String sortBy, int page, int pageSize, SearchIntent intent) {
//
//        List<SearchResultDto> results = new ArrayList<>();
//
//        // 1. Búsqueda exacta (peso alto)
//        for (String query : queries) {
//            SearchResultDto exactResult = searchIndexService.exactSearch(query, filters, page, pageSize);
//            if (exactResult.getTotalResults() > 0) {
//                results.add(weightResults(exactResult, 1.0));
//            }
//        }
//
//        // 2. Búsqueda fuzzy (peso medio)
//        for (String query : queries) {
//            SearchResultDto fuzzyResult = searchIndexService.fuzzySearch(query, filters, page, pageSize);
//            results.add(weightResults(fuzzyResult, 0.7));
//        }
//
//        // 3. Búsqueda semántica (peso medio-bajo)
//        SearchResultDto semanticResult = searchIndexService.semanticSearch(queries.get(0), filters, page, pageSize);
//        results.add(weightResults(semanticResult, 0.5));
//
//        // 4. Combinar y ordenar resultados
//        return combineSearchResults(results, sortBy, intent, page, pageSize);
//    }
//
//    /**
//     * Personaliza resultados basado en historial del cliente
//     */
//    private SearchResultDto personalizeResults(SearchResultDto result, Long customerId) {
//        // Obtener preferencias del cliente
//        Map<String, Double> customerPreferences = searchAnalyticsService.getCustomerPreferences(customerId);
//
//        // Boost productos de categorías/marcas preferidas
//        result.getProducts().forEach(product -> {
//            double boost = 1.0;
//
//            // Boost por categoría preferida
//            if (customerPreferences.containsKey("category_" + product.getCategory())) {
//                boost += customerPreferences.get("category_" + product.getCategory()) * 0.2;
//            }
//
//            // Boost por marca preferida
//            if (customerPreferences.containsKey("brand_" + product.getBrand())) {
//                boost += customerPreferences.get("brand_" + product.getBrand()) * 0.15;
//            }
//
//            // Aplicar boost
//            BigDecimal newScore = product.getRelevanceScore().multiply(BigDecimal.valueOf(boost));
//            product.setRelevanceScore(newScore);
//        });
//
//        // Reordenar por nueva puntuación
//        result.getProducts().sort((p1, p2) -> p2.getRelevanceScore().compareTo(p1.getRelevanceScore()));
//
//        return result;
//    }
//
//    /**
//     * Genera sugerencias de búsqueda
//     */
//    private List<String> generateSuggestions(String query) {
//        return autocompleteService.getSuggestions(query, 5);
//    }
//
//    /**
//     * Genera correcciones ortográficas
//     */
//    private List<String> generateCorrections(String query) {
//        return autocompleteService.getSpellCorrections(query, 3);
//    }
//
//    /**
//     * Aplica peso a los resultados de búsqueda
//     */
//    private SearchResultDto weightResults(SearchResultDto result, double weight) {
//        result.getProducts().forEach(product -> {
//            BigDecimal weightedScore = product.getRelevanceScore().multiply(BigDecimal.valueOf(weight));
//            product.setRelevanceScore(weightedScore);
//        });
//        return result;
//    }
//
//    /**
//     * Combina múltiples resultados de búsqueda
//     */
//    private SearchResultDto combineSearchResults(List<SearchResultDto> results, String sortBy,
//                                               SearchIntent intent, int page, int pageSize) {
//        // Combinar todos los productos
//        Map<Long, Object> uniqueProducts = new HashMap<>();
//
//        for (SearchResultDto result : results) {
//            for (Object product : result.getProducts()) {
//                // Lógica para combinar productos duplicados con mejor puntuación
//                // uniqueProducts.merge(product.getId(), product, this::mergeDuplicateProducts);
//            }
//        }
//
//        // Aplicar ordenamiento basado en intención
//        List<Object> sortedProducts = sortProductsByIntent(new ArrayList<>(uniqueProducts.values()), intent, sortBy);
//
//        // Paginación
//        int start = page * pageSize;
//        int end = Math.min(start + pageSize, sortedProducts.size());
//        List<Object> paginatedProducts = sortedProducts.subList(start, end);
//
//        return SearchResultDto.builder()
//                .totalResults(sortedProducts.size())
//                .page(page)
//                .pageSize(pageSize)
//                .products((List) paginatedProducts)
//                .build();
//    }
//
//    /**
//     * Ordena productos basado en la intención de búsqueda
//     */
//    private List<Object> sortProductsByIntent(List<Object> products, SearchIntent intent, String sortBy) {
//        // Implementar lógica de ordenamiento específica por intención
//        return products; // Simplificado
//    }
//
//    /**
//     * Búsqueda simple como fallback
//     */
//    private SearchResultDto executeSimpleSearch(String query, Map<String, List<String>> filters,
//                                              String sortBy, int page, int pageSize) {
//        return searchIndexService.simpleSearch(query, filters, sortBy, page, pageSize);
//    }
//
//    /**
//     * Genera ID único para la búsqueda
//     */
//    private String generateSearchId() {
//        return "search_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
//    }
//
//    /**
//     * Enum para intenciones de búsqueda
//     */
//    private enum SearchIntent {
//        GENERAL, PRICE_FOCUSED, BRAND_FOCUSED, ATTRIBUTE_FOCUSED, QUALITY_FOCUSED
//    }
//}
