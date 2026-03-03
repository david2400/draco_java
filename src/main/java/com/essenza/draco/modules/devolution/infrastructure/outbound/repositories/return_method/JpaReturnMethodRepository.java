package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.return_method;

import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.ReturnMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaReturnMethodRepository extends JpaRepository<ReturnMethodEntity, Long> {
    Optional<ReturnMethodEntity> findByName(String name);
}
