package com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop;

import java.util.ArrayList;
import java.util.List;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Plantilla de producto (tabla {@code product_templates}, V5; antes {@code type_products}). */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_templates")
public class ProductTemplateEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template")
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Version
    @Column(nullable = false)
    private Long version;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<TemplateAttributeEntity> attributes = new ArrayList<>();
}
