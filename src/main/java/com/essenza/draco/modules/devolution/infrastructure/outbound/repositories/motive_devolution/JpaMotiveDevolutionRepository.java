package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.motive_devolution;

import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.MotiveDevolutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaMotiveDevolutionRepository extends JpaRepository<MotiveDevolutionEntity, Long> {
    Optional<MotiveDevolutionEntity> findByName(String name);
}
