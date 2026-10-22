package com.essenza.draco.modules.billing.application.services.resolution;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.resolution.ResolutionDto;
import com.essenza.draco.modules.billing.application.dto.resolution.SaveResolutionDto;
import com.essenza.draco.modules.billing.application.input.resolution.CreateResolutionUseCase;
import com.essenza.draco.modules.billing.application.output.repository.ResolutionRepository;

@Service
@Transactional
public class CreateResolutionService implements CreateResolutionUseCase {

    private final ResolutionSoporte soporte;
    private final ResolutionRepository resolutions;

    public CreateResolutionService(ResolutionSoporte soporte, ResolutionRepository resolutions) {
        this.soporte = soporte;
        this.resolutions = resolutions;
    }

    @Override
    public ResolutionDto create(SaveResolutionDto input) {
        return soporte.toDto(resolutions.save(soporte.toDomain(null, input, null)));
    }
}
