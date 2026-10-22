package com.essenza.draco.modules.inventory.application.output.repository;

import com.essenza.draco.modules.inventory.domain.model.supplier.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Persistencia de la ficha del proveedor (con sus contactos). */
public interface SupplierRepository {

    Optional<Supplier> findById(Long id);

    /** Guarda la ficha y reemplaza sus contactos. */
    Supplier save(Supplier supplier);

    void delete(Long id);

    Page<Supplier> search(SupplierFilter filter, Pageable pageable);

    List<Supplier> findAll();

    boolean existsCode(String code, Long excludeId);

    boolean existsDocument(String docType, String docNumber, Long excludeId);

    /** Siguiente número para el código automático PROV_<n>. */
    long nextNumber();

    /** SKU activos que surte cada proveedor. */
    Map<Long, Long> countProducts(Collection<Long> supplierIds);

    /** Productos del catálogo (no borrados) que lo tienen como proveedor principal. */
    long countCatalogProducts(Long supplierId);

    Counts counts();

    record Counts(long total, long active, long blocked, long inactive, long withoutDocument, long credit) {
    }
}
