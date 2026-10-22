package com.essenza.draco.modules.billing.application.services.resolution;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.resolution.ResolutionDto;
import com.essenza.draco.modules.billing.application.dto.resolution.SaveResolutionDto;
import com.essenza.draco.modules.billing.application.input.resolution.UpdateResolutionUseCase;
import com.essenza.draco.modules.billing.application.output.repository.ResolutionRepository;
import com.essenza.draco.modules.billing.domain.model.NumberingResolution;
import com.essenza.draco.shared.exceptions.ConflictException;

/** Con documentos emitidos no cambian tipo, prefijo ni número inicial, y el consecutivo no retrocede. */
@Service
@Transactional
public class UpdateResolutionService implements UpdateResolutionUseCase {

    private final ResolutionSoporte soporte;
    private final ResolutionRepository resolutions;

    public UpdateResolutionService(ResolutionSoporte soporte, ResolutionRepository resolutions) {
        this.soporte = soporte;
        this.resolutions = resolutions;
    }

    @Override
    public ResolutionDto update(Long id, SaveResolutionDto input) {
        NumberingResolution current = soporte.get(id);
        NumberingResolution next = soporte.toDomain(id, input, current);
        boolean used = current.used() || resolutions.documentsUsing(id) > 0;
        if (used && (next.kind() != current.kind() || !Objects.equals(next.prefix(), current.prefix())
                || next.fromNumber() != current.fromNumber())) {
            throw new ConflictException("La numeración ya tiene documentos: no se cambian el tipo, el prefijo ni el número inicial.");
        }
        if (next.nextNumber() < current.nextNumber()) {
            throw new ConflictException("El consecutivo no puede retroceder (va en " + current.nextNumber() + ").");
        }
        return soporte.toDto(resolutions.save(next));
    }
}
