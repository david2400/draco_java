package com.essenza.draco.modules.billing.application.services.resolution;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.input.resolution.DeleteResolutionUseCase;
import com.essenza.draco.modules.billing.application.output.repository.ResolutionRepository;
import com.essenza.draco.shared.exceptions.ConflictException;

@Service
@Transactional
public class DeleteResolutionService implements DeleteResolutionUseCase {

    private final ResolutionSoporte soporte;
    private final ResolutionRepository resolutions;

    public DeleteResolutionService(ResolutionSoporte soporte, ResolutionRepository resolutions) {
        this.soporte = soporte;
        this.resolutions = resolutions;
    }

    @Override
    public void delete(Long id) {
        soporte.get(id);
        if (resolutions.documentsUsing(id) > 0) {
            throw new ConflictException("La numeración tiene documentos emitidos: desactívala en lugar de eliminarla.");
        }
        resolutions.delete(id);
    }
}
