package com.essenza.draco.modules.billing.infrastructure.outbound.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.billing.application.output.repository.TaxRateRepository;
import com.essenza.draco.modules.billing.domain.model.TaxKind;
import com.essenza.draco.modules.billing.domain.model.TaxRate;
import com.essenza.draco.modules.billing.infrastructure.outbound.persistence.TaxRateEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Repository
public class TaxRateRepositoryAdapter implements TaxRateRepository {

    private final JpaTaxRateRepository jpa;

    public TaxRateRepositoryAdapter(JpaTaxRateRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<TaxRate> findAll() {
        return jpa.findAll().stream().map(TaxRateRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<TaxRate> findById(Long id) {
        return jpa.findById(id).map(TaxRateRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<TaxRate> findDefault() {
        return jpa.findByIsDefaultTrueOrderByIdAsc().stream().findFirst().map(TaxRateRepositoryAdapter::toDomain);
    }

    @Override
    public TaxRate save(TaxRate rate) {
        TaxRateEntity entity = rate.id() == null ? new TaxRateEntity()
                : jpa.findById(rate.id()).orElseThrow(() -> new NotFoundException("Tarifa no encontrada: " + rate.id()));
        entity.setCode(rate.code());
        entity.setName(rate.name());
        entity.setDescription(rate.description());
        entity.setKind(rate.kind().name());
        entity.setRate(rate.rate());
        entity.setIsDefault(rate.isDefault());
        entity.setActive(rate.active());
        entity.setPosition(rate.position());
        return toDomain(jpa.saveAndFlush(entity));
    }

    @Override
    public void delete(Long id) {
        jpa.findById(id).ifPresent(entity -> {
            jpa.delete(entity);
            jpa.flush();
        });
    }

    @Override
    public boolean existsCode(String code, Long excludeId) {
        return jpa.existsCode(code, excludeId);
    }

    @Override
    public void clearDefaultExcept(Long id) {
        jpa.clearDefaultExcept(id);
    }

    @Override
    public long productsUsing(Long id) {
        return jpa.productsUsing(id);
    }

    @Override
    public long linesUsing(Long id) {
        return jpa.linesUsing(id);
    }

    static TaxRate toDomain(TaxRateEntity e) {
        return new TaxRate(e.getId(), e.getCode(), e.getName(), e.getDescription(),
                TaxKind.tryParse(e.getKind()).orElse(TaxKind.IVA), e.getRate(), Boolean.TRUE.equals(e.getIsDefault()),
                !Boolean.FALSE.equals(e.getActive()), e.getPosition() == null ? 0 : e.getPosition());
    }
}
