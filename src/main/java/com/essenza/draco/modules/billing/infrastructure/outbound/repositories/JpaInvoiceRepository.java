package com.essenza.draco.modules.billing.infrastructure.outbound.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.essenza.draco.modules.billing.infrastructure.outbound.persistence.InvoiceEntity;

import jakarta.persistence.LockModeType;

public interface JpaInvoiceRepository extends JpaRepository<InvoiceEntity, Long>, JpaSpecificationExecutor<InvoiceEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InvoiceEntity i where i.id = :id")
    Optional<InvoiceEntity> lockById(@Param("id") Long id);

    List<InvoiceEntity> findByOrderIdOrderByIdAsc(Long orderId);

    @Query("select i from InvoiceEntity i where i.orderId = :orderId and i.kind = 'INVOICE' and i.status <> 'VOIDED' order by i.id desc")
    List<InvoiceEntity> activeInvoicesFor(@Param("orderId") Long orderId);

    List<InvoiceEntity> findByKindAndRelatedInvoiceIdOrderByIdDesc(String kind, Long relatedInvoiceId);

    Optional<InvoiceEntity> findByDevolutionId(Long devolutionId);
}
