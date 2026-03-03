// package com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop;

// import com.essenza.draco.shared.common.domain.entity.AuditInfo;
// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.experimental.SuperBuilder;

// import java.util.List;

// @SuperBuilder
// @Data
// @AllArgsConstructor
// @NoArgsConstructor
// @Entity
// @Table(name = "combos")
// public class ComboEntity extends AuditInfo {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "id_combo")
//     private Long id;

//     @Column(nullable = false)
//     private String name;

//     @Column(nullable = false, length = 255)
//     private String description;

//     @Column(name = "image_url")
//     private String imageUrl;

//     @Column(nullable = false)
//     private Boolean available = true;

//     @Column(name = "real_price", nullable = false)
//     private Double realPrice;

//     @Column(nullable = false)
//     private Double price;

//     @OneToMany(mappedBy = "combo", fetch = FetchType.LAZY)
//     private List<ProductComboEntity> productCombo;

// }
