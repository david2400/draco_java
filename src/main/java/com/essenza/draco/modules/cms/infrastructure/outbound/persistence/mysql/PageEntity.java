package com.essenza.draco.modules.cms.infrastructure.outbound.persistence.mysql;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "cms_pages")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PageEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_page")
    private Long id;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 255)
    private String excerpt;

    @Column(name = "meta_title", length = 180)
    private String metaTitle;

    @Column(name = "meta_description", length = 255)
    private String metaDescription;

    @Column(name = "meta_keywords", length = 255)
    private String metaKeywords;

    @Column(length = 30)
    private String status;

    @Column(length = 80)
    private String template;

    @Column(name = "is_featured")
    private Boolean isFeatured;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "author_name", length = 120)
    private String authorName;

    @Column(name = "featured_image", length = 255)
    private String featuredImage;

    @Column(name = "page_type", length = 40)
    private String pageType;

    @Lob
    @Column(name = "custom_fields", columnDefinition = "LONGTEXT")
    private String customFields;
}
