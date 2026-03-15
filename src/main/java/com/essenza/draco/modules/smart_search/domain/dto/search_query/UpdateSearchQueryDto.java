package com.essenza.draco.modules.smart_search.domain.dto.search_query;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSearchQueryDto {
    private Long customerId;

    @Size(max = 255)
    private String query;

    private String filtersJson;

    @Size(max = 50)
    private String sortBy;

    @Min(0)
    private Integer page;

    @Min(1)
    private Integer pageSize;

    private Integer totalResults;

    @Size(max = 100)
    private String searchId;

    private Instant lastRunAt;
}
