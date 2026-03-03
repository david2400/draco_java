package com.essenza.draco.modules.smart_search.domain.dto.search_result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDto {
    private String query;
    private Integer totalResults;
    private Integer page;
    private Integer pageSize;
    private Long searchTimeMs;
    private List<ProductSearchDto> products;
    private Map<String, List<FacetDto>> facets;
    private List<String> suggestions;
    private List<String> corrections;
    private String searchId; // Para analytics
}

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
class ProductSearchDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private BigDecimal rating;
    private Integer reviewCount;
    private String brand;
    private String category;
    private Boolean inStock;
    private BigDecimal relevanceScore;
    private List<String> highlights; // Texto resaltado
}

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
class FacetDto {
    private String value;
    private Integer count;
    private Boolean selected;
}
