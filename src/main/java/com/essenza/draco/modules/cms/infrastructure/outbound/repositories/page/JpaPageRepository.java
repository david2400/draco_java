package com.essenza.draco.modules.cms.infrastructure.outbound.repositories.page;

import com.essenza.draco.modules.cms.infrastructure.outbound.persistence.mysql.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPageRepository extends JpaRepository<PageEntity, Long> {

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    Optional<PageEntity> findBySlug(String slug);
}
