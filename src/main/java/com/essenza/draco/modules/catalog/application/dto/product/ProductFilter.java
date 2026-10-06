package com.essenza.draco.modules.catalog.application.dto.product;

import java.util.List;

public class ProductFilter {
    private List<Long> brandIds;
    private List<Long> categoryIds;
    private List<Long> subcategoryIds;
    private Double minPrice;
    private Double maxPrice;
    private Boolean available;
    private String search;
    /** Estados editoriales a incluir (DRAFT, ACTIVE, INACTIVE, ARCHIVED). */
    private List<String> statuses;

    public List<Long> getBrandIds() { return brandIds; }
    public void setBrandIds(List<Long> brandIds) { this.brandIds = brandIds; }

    public List<Long> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<Long> categoryIds) { this.categoryIds = categoryIds; }

    public List<Long> getSubcategoryIds() { return subcategoryIds; }
    public void setSubcategoryIds(List<Long> subcategoryIds) { this.subcategoryIds = subcategoryIds; }

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public List<String> getStatuses() { return statuses; }
    public void setStatuses(List<String> statuses) { this.statuses = statuses; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
}
