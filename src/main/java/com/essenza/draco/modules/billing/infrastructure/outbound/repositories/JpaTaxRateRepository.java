package com.essenza.draco.modules.billing.infrastructure.outbound.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.essenza.draco.modules.billing.infrastructure.outbound.persistence.TaxRateEntity;

public interface JpaTaxRateRepository extends JpaRepository<TaxRateEntity, Long> {

    List<TaxRateEntity> findByIsDefaultTrueOrderByIdAsc();

    @Query("select count(t) > 0 from TaxRateEntity t where t.code = :code and (:excludeId is null or t.id <> :excludeId)")
    boolean existsCode(@Param("code") String code, @Param("excludeId") Long excludeId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update TaxRateEntity t set t.isDefault = false where t.isDefault = true and t.id <> :id")
    int clearDefaultExcept(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM products WHERE tax_rate_id = :id AND deleted = 0", nativeQuery = true)
    long productsUsing(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM product_orders WHERE tax_rate_id = :id AND deleted = 0", nativeQuery = true)
    long linesUsing(@Param("id") Long id);
}
