package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.attribute;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.AttributeEntity;

public interface JpaAttributeRepository extends JpaRepository<AttributeEntity, Long> {

    @Query("select distinct a from AttributeEntity a left join fetch a.options order by a.name")
    List<AttributeEntity> findAllWithOptions();

    @Query("select distinct a from AttributeEntity a left join fetch a.options where a.id in :ids")
    List<AttributeEntity> findAllWithOptionsByIdIn(java.util.Collection<Long> ids);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
