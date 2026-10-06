package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.attribute;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductTemplateEntity;

public interface JpaProductTemplateRepository extends JpaRepository<ProductTemplateEntity, Long> {

    @Query("select distinct t from ProductTemplateEntity t left join fetch t.attributes order by t.name")
    List<ProductTemplateEntity> findAllWithAttributes();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
