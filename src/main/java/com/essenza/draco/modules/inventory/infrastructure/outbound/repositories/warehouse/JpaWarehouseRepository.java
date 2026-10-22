package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.warehouse;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaWarehouseRepository extends JpaRepository<WarehouseEntity, Long>, JpaSpecificationExecutor<WarehouseEntity> {

    Optional<WarehouseEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    Optional<WarehouseEntity> findFirstByIsMainTrueOrderByIdAsc();

    Optional<WarehouseEntity> findFirstByActiveTrueOrderByIdAsc();

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE WarehouseEntity w SET w.isMain = false WHERE w.isMain = true AND w.id <> :exceptId")
    int clearMain(@Param("exceptId") Long exceptId);
}
