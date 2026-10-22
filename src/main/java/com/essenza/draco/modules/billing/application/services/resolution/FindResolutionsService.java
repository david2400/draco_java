package com.essenza.draco.modules.billing.application.services.resolution;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.resolution.ResolutionDto;
import com.essenza.draco.modules.billing.application.input.resolution.FindResolutionsUseCase;
import com.essenza.draco.modules.billing.application.output.repository.ResolutionRepository;

@Service
@Transactional(readOnly = true)
public class FindResolutionsService implements FindResolutionsUseCase {

    private final ResolutionSoporte soporte;
    private final ResolutionRepository resolutions;

    public FindResolutionsService(ResolutionSoporte soporte, ResolutionRepository resolutions) {
        this.soporte = soporte;
        this.resolutions = resolutions;
    }

    @Override
    public List<ResolutionDto> findAll(String kind) {
        return resolutions.findAll(kind == null || kind.isBlank() ? null : ResolutionSoporte.kind(kind)).stream()
                .map(soporte::toDto).toList();
    }

    @Override
    public ResolutionDto findById(Long id) {
        return soporte.toDto(soporte.get(id));
    }
}
