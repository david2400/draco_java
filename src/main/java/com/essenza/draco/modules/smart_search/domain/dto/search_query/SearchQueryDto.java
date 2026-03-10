package com.essenza.draco.modules.smart_search.domain.dto.search_query;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryDto extends AuditInfoDto {
    private Long id;
    private Long customerId;
    private String query;
    private String filtersJson;
    private String sortBy;
    private Integer page;
    private Integer pageSize;
    private Integer totalResults;
    private Instant lastRunAt;
    private String searchId;
}
