package com.essenza.draco.modules.smart_search.infrastructure.outbound.persistence.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSearchQueryRepository extends JpaRepository<SearchQueryEntity, Long> {
}
