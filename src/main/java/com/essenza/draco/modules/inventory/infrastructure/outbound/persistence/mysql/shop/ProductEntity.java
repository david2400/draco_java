package com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop;


import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.BrandEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.CategoryEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.SubcategoryEntity;
import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class ProductEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "real_price", nullable = false)
    private Double realPrice;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(nullable = true)
    private Double length;

    @Column(nullable = true)
    private Double width;

    @Column(nullable = true)
    private Double height;

    @Column(nullable = true)
    private Double weight;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private Boolean available = true;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", insertable = false, updatable = false)
    private BrandEntity brand;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private CategoryEntity category;

    @Column(name = "subcategory_id", nullable = false)
    private Long subcategoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subcategory_id", insertable = false, updatable = false)
    private SubcategoryEntity subcategory;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", insertable = false, updatable = false)
    private SupplierEntity supplier;

    @Column(name = "is_combo", nullable = false)
    private Boolean isCombo = false;

    @OneToMany(mappedBy = "combo", fetch = FetchType.LAZY)
    private List<ProductComboEntity> productCombos;

}
