package com.essenza.draco.modules.analytics.infrastructure.outbound.persistence.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSalesAnalyticsRecordRepository extends JpaRepository<SalesAnalyticsRecordEntity, Long> {
}
