package com.essenza.draco.modules.cms.domain.dto.page;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto extends AuditInfoDto {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private String status; // DRAFT, PUBLISHED, ARCHIVED
    private String template;
    private Boolean isFeatured;
    private Integer sortOrder;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private String authorName;
    private String featuredImage;
    private String pageType; // STATIC, LANDING, BLOG, CATEGORY
    private String customFields; // JSON string for additional fields
}
