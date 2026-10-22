package com.essenza.draco.modules.billing.application.services.resolution;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.essenza.draco.modules.billing.application.dto.resolution.ResolutionDto;
import com.essenza.draco.modules.billing.application.dto.resolution.SaveResolutionDto;
import com.essenza.draco.modules.billing.application.output.repository.ResolutionRepository;
import com.essenza.draco.modules.billing.domain.model.BillingEnvironment;
import com.essenza.draco.modules.billing.domain.model.BillingRuleViolation;
import com.essenza.draco.modules.billing.domain.model.DocumentKind;
import com.essenza.draco.modules.billing.domain.model.NumberingResolution;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Component
public class ResolutionSoporte {

    static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    /** Avisos: vence en ≤ 30 días o quedan menos de 100 números (o del 10 % del rango). */
    static final long EXPIRING_DAYS = 30;
    static final long LOW_REMAINING = 100;

    private final ResolutionRepository resolutions;

    ResolutionSoporte(ResolutionRepository resolutions) {
        this.resolutions = resolutions;
    }

    NumberingResolution get(Long id) {
        return resolutions.findById(id).orElseThrow(() -> new NotFoundException("No se encontró la numeración " + id + "."));
    }

    static DocumentKind kind(String value) {
        try {
            return DocumentKind.valueOf(value == null ? "" : value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BillingRuleViolation("Tipo de numeración no válido: " + value + " (INVOICE o CREDIT_NOTE).");
        }
    }

    NumberingResolution toDomain(Long id, SaveResolutionDto input, NumberingResolution current) {
        long next = input.getNextNumber() != null ? input.getNextNumber()
                : current != null ? current.nextNumber() : input.getFromNumber();
        return new NumberingResolution(id, kind(input.getKind()), input.getPrefix(), input.getResolutionNumber(),
                input.getResolutionDate(), input.getTechnicalKey(), input.getFromNumber(), input.getToNumber(), next,
                input.getValidFrom(), input.getValidTo(), BillingEnvironment.parse(input.getEnvironment()),
                input.getActive() == null || input.getActive());
    }

    public static ResolutionDto toDto(NumberingResolution r, boolean used) {
        LocalDate today = LocalDate.now(BOGOTA);
        Long days = r.validTo() == null ? null : ChronoUnit.DAYS.between(today, r.validTo());
        List<String> alerts = new ArrayList<>();
        long range = r.toNumber() - r.fromNumber() + 1;
        if (days != null && days < 0) {
            alerts.add("EXPIRED");
        } else if (days != null && days <= EXPIRING_DAYS) {
            alerts.add("EXPIRING");
        }
        if (r.remaining() == 0) {
            alerts.add("EXHAUSTED");
        } else if (r.remaining() < Math.min(LOW_REMAINING, Math.max(1, range / 10))) {
            alerts.add("LOW");
        }
        return ResolutionDto.builder()
                .id(r.id())
                .kind(r.kind().name())
                .prefix(r.prefix())
                .resolutionNumber(r.resolutionNumber())
                .resolutionDate(r.resolutionDate())
                .technicalKey(r.technicalKey())
                .fromNumber(r.fromNumber())
                .toNumber(r.toNumber())
                .nextNumber(r.nextNumber())
                .validFrom(r.validFrom())
                .validTo(r.validTo())
                .environment(r.environment().name())
                .active(r.active())
                .remaining(r.remaining())
                .daysToExpire(days)
                .used(used)
                .alerts(alerts)
                .build();
    }

    ResolutionDto toDto(NumberingResolution r) {
        return toDto(r, r.id() != null && (r.used() || resolutions.documentsUsing(r.id()) > 0));
    }
}
