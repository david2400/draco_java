package com.essenza.draco.modules.billing.infrastructure.outbound.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.essenza.draco.modules.billing.infrastructure.outbound.persistence.InvoiceLineEntity;

public interface JpaInvoiceLineRepository extends JpaRepository<InvoiceLineEntity, Long> {

    List<InvoiceLineEntity> findByInvoiceIdOrderByPositionAsc(Long invoiceId);

    List<InvoiceLineEntity> findByInvoiceIdInOrderByInvoiceIdAscPositionAsc(Collection<Long> invoiceIds);
}
