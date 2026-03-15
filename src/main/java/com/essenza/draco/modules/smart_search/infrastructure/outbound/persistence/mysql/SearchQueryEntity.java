package com.essenza.draco.modules.smart_search.infrastructure.outbound.persistence.mysql;

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

import java.time.Instant;

@Entity
@Table(name = "search_queries")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_search_query")
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(nullable = false, length = 255)
    private String query;

    @Lob
    @Column(name = "filters_json", columnDefinition = "LONGTEXT")
    private String filtersJson;

    @Column(name = "sort_by", length = 50)
    private String sortBy;

    @Column(nullable = false)
    private Integer page;

    @Column(name = "page_size", nullable = false)
    private Integer pageSize;

    @Column(name = "total_results")
    private Integer totalResults;

    @Column(name = "last_run_at")
    private Instant lastRunAt;

    @Column(name = "search_id", length = 100)
    private String searchId;
}
