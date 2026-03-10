package com.essenza.draco.modules.cms.domain.dto.page;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePageDto {
    @NotBlank
    @Size(max = 180)
    private String title;

    @Size(max = 200)
    private String slug;

    @NotBlank
    private String content;

    @Size(max = 255)
    private String excerpt;

    @Size(max = 180)
    private String metaTitle;

    @Size(max = 255)
    private String metaDescription;

    @Size(max = 255)
    private String metaKeywords;

    @Size(max = 30)
    private String status;

    @Size(max = 80)
    private String template;

    private Boolean isFeatured;

    private Integer sortOrder;

    private LocalDateTime publishedAt;

    private LocalDateTime scheduledAt;

    @Size(max = 120)
    private String authorName;

    @Size(max = 255)
    private String featuredImage;

    @Size(max = 40)
    private String pageType;

    private String customFields;
}
