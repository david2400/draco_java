package com.essenza.draco.modules.advanced_features.infrastructure.outbound.persistence.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPersonalizationProfileRepository extends JpaRepository<PersonalizationProfileEntity, Long> {
}
