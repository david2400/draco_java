package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SaveSupplierDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierContactDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierProductDto;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierProductRepository;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierRepository;
import com.essenza.draco.modules.inventory.domain.model.supplier.BankAccount;
import com.essenza.draco.modules.inventory.domain.model.supplier.CommercialTerms;
import com.essenza.draco.modules.inventory.domain.model.supplier.ContactArea;
import com.essenza.draco.modules.inventory.domain.model.supplier.PersonType;
import com.essenza.draco.modules.inventory.domain.model.supplier.Supplier;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierContact;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierProduct;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierStatus;
import com.essenza.draco.modules.inventory.domain.model.supplier.TaxId;
import com.essenza.draco.modules.inventory.domain.model.supplier.TaxRegime;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/** Lógica compartida por los casos de uso de proveedores: carga, armado desde el formulario y DTO. */
@Component
class SupplierSoporte {

    private final SupplierRepository suppliers;
    SupplierSoporte(SupplierRepository suppliers) {
        this.suppliers = suppliers;
    }

    Supplier supplier(Long id) {
        return suppliers.findById(id).orElseThrow(() -> new NotFoundException("No se encontró el proveedor " + id + "."));
    }

    /** Arma la ficha desde el formulario; el estado lo decide quien llama (alta o edición). */
    Supplier from(Long id, SaveSupplierDto in, SupplierStatus status, String blockReason) {
        String code = in.code() == null || in.code().isBlank() ? "PROV_" + suppliers.nextNumber() : in.code();
        List<SupplierContact> contacts = in.contacts() == null ? List.of() : in.contacts().stream()
                .map(c -> new SupplierContact(null, c.name(), c.position(), ContactArea.of(c.area()), c.phone(), c.email(),
                        Boolean.TRUE.equals(c.primary())))
                .toList();
        CommercialTerms terms = new CommercialTerms(in.paymentTermDays() == null ? 0 : in.paymentTermDays(), in.creditLimit(),
                in.currency(), in.leadTimeDays(), in.minOrderAmount(), in.discountPercent());
        BankAccount bank = BankAccount.of(in.bankName(), in.bankAccountType(), in.bankAccountNumber(), in.bankAccountHolder(),
                in.bankHolderDocument());
        return new Supplier(id, code, TaxId.of(in.docType(), in.docNumber()), PersonType.of(in.personType()), in.name(),
                in.tradeName(), TaxRegime.of(in.taxRegime()), Boolean.TRUE.equals(in.greatContributor()),
                Boolean.TRUE.equals(in.selfRetainer()), in.email(), in.phone(), in.website(), in.address(),
                in.countryId(), in.stateId(), in.cityId(), in.city(), status, blockReason, terms, bank, in.notes(), contacts)
                .requireDocument();
    }

    /** Código y documento únicos entre los proveedores no borrados. */
    void checkUnique(Supplier supplier, Long excludeId) {
        if (suppliers.existsCode(supplier.code(), excludeId)) {
            throw new ConflictException("Ya existe un proveedor con el código " + supplier.code() + ".");
        }
        if (supplier.taxId() != null
                && suppliers.existsDocument(supplier.taxId().type(), supplier.taxId().number(), excludeId)) {
            throw new ConflictException("Ya existe un proveedor con el documento " + supplier.taxId().type() + " "
                    + supplier.taxId().formatted() + ".");
        }
    }

    SupplierDto detail(Supplier s) {
        long count = suppliers.countProducts(List.of(s.id())).getOrDefault(s.id(), 0L);
        return toDto(s, true, count, suppliers.countCatalogProducts(s.id()));
    }

    List<SupplierDto> summaries(List<Supplier> list) {
        Map<Long, Long> counts = suppliers.countProducts(list.stream().map(Supplier::id).toList());
        return list.stream().map(s -> toDto(s, false, counts.getOrDefault(s.id(), 0L), -1)).toList();
    }

    SupplierDto toDto(Supplier s, boolean withContacts, long productsCount, long catalogProducts) {
        TaxId tax = s.taxId();
        BankAccount bank = s.bank();
        CommercialTerms terms = s.terms();
        SupplierContact primary = s.primaryContact();
        List<SupplierContactDto> contacts = withContacts
                ? s.contacts().stream().map(c -> new SupplierContactDto(c.id(), c.name(), c.position(), c.area().name(),
                        c.phone(), c.email(), c.primary())).toList()
                : List.of();
        return new SupplierDto(s.id(), s.code(), tax == null ? null : tax.type(), tax == null ? null : tax.number(),
                tax == null ? null : tax.dv(), tax == null ? null : tax.formatted(), s.personType().name(), s.name(),
                s.tradeName(), s.displayName(), s.taxRegime() == null ? null : s.taxRegime().name(), s.greatContributor(),
                s.selfRetainer(), s.email(), s.phone(), s.website(), s.address(), s.countryId(), s.stateId(), s.cityId(),
                s.city(), s.status().name(), s.blockReason(), terms.paymentTermDays(), terms.creditLimit(), terms.currency(),
                terms.leadTimeDays(), terms.minOrderAmount(), terms.discountPercent(),
                bank == null ? null : bank.bankName(), bank == null ? null : bank.accountType(),
                bank == null ? null : bank.accountNumber(), bank == null ? null : bank.masked(),
                bank == null ? null : bank.holderName(), bank == null ? null : bank.holderDocument(), s.notes(), contacts,
                primary == null ? null : primary.name(), primary == null ? null : primary.phone(),
                primary == null ? null : primary.email(), productsCount, Math.max(catalogProducts, 0));
    }

    SupplierProductDto productDto(SupplierProductRepository.View view, Supplier supplier) {
        SupplierProduct p = view.product();
        Integer effective = p.leadTimeDays() != null ? p.leadTimeDays()
                : supplier == null ? null : supplier.terms().leadTimeDays();
        return new SupplierProductDto(p.id(), p.supplierId(), p.productId(), p.skuId(), view.skuCode(), view.productName(),
                view.skuName(), view.salePrice(), p.unitCost(), p.marginPercent(view.salePrice()), p.supplierCode(),
                p.leadTimeDays(), effective, p.minOrderQty(), p.preferred(), p.active());
    }
}
