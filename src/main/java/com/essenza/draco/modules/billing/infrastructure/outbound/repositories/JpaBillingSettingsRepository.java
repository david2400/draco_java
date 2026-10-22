package com.essenza.draco.modules.billing.infrastructure.outbound.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.essenza.draco.modules.billing.infrastructure.outbound.persistence.BillingSettingsEntity;

public interface JpaBillingSettingsRepository extends JpaRepository<BillingSettingsEntity, Long> {
}
