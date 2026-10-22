package com.essenza.draco.modules.billing.infrastructure.outbound.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.billing.application.output.repository.ResolutionRepository;
import com.essenza.draco.modules.billing.domain.model.BillingEnvironment;
import com.essenza.draco.modules.billing.domain.model.DocumentKind;
import com.essenza.draco.modules.billing.domain.model.NumberingResolution;
import com.essenza.draco.modules.billing.infrastructure.outbound.persistence.BillingResolutionEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Repository
public class ResolutionRepositoryAdapter implements ResolutionRepository {

    private final JpaBillingResolutionRepository jpa;

    public ResolutionRepositoryAdapter(JpaBillingResolutionRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<NumberingResolution> findAll(DocumentKind kind) {
        List<BillingResolutionEntity> rows = kind == null ? jpa.findAllByOrderByKindAscIdDesc()
                : jpa.findByKindOrderByIdDesc(kind.name());
        return rows.stream().map(ResolutionRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<NumberingResolution> findById(Long id) {
        return jpa.findById(id).map(ResolutionRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<NumberingResolution> lockActive(DocumentKind kind, BillingEnvironment environment) {
        return jpa.lockActive(kind.name(), environment.name()).stream().findFirst().map(ResolutionRepositoryAdapter::toDomain);
    }

    @Override
    public NumberingResolution save(NumberingResolution r) {
        BillingResolutionEntity e = r.id() == null ? new BillingResolutionEntity()
                : jpa.findById(r.id()).orElseThrow(() -> new NotFoundException("Numeración no encontrada: " + r.id()));
        e.setKind(r.kind().name());
        e.setPrefix(r.prefix());
        e.setResolutionNumber(r.resolutionNumber());
        e.setResolutionDate(r.resolutionDate());
        e.setTechnicalKey(r.technicalKey());
        e.setFromNumber(r.fromNumber());
        e.setToNumber(r.toNumber());
        e.setNextNumber(r.nextNumber());
        e.setValidFrom(r.validFrom());
        e.setValidTo(r.validTo());
        e.setEnvironment(r.environment().name());
        e.setActive(r.active());
        return toDomain(jpa.saveAndFlush(e));
    }

    @Override
    public void delete(Long id) {
        jpa.findById(id).ifPresent(entity -> {
            jpa.delete(entity);
            jpa.flush();
        });
    }

    @Override
    public long documentsUsing(Long id) {
        return jpa.documentsUsing(id);
    }

    static NumberingResolution toDomain(BillingResolutionEntity e) {
        return new NumberingResolution(e.getId(), DocumentKind.valueOf(e.getKind()), e.getPrefix(), e.getResolutionNumber(),
                e.getResolutionDate(), e.getTechnicalKey(), e.getFromNumber(), e.getToNumber(), e.getNextNumber(),
                e.getValidFrom(), e.getValidTo(), BillingEnvironment.parse(e.getEnvironment()), !Boolean.FALSE.equals(e.getActive()));
    }
}
