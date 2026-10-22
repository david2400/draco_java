package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.order_devolution;

import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.OrderDevolutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderDevolutionRepository extends JpaRepository<OrderDevolutionEntity, Long>, JpaSpecificationExecutor<OrderDevolutionEntity> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select d from OrderDevolutionEntity d where d.id = :id")
    java.util.Optional<OrderDevolutionEntity> findForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}
