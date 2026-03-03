package com.essenza.draco.modules.devolution.application.input.evidence;

import com.essenza.draco.modules.devolution.domain.dto.evidence.EvidenceDto;

import java.util.Optional;

public interface FindEvidenceByIdUseCase {
    Optional<EvidenceDto> findById(Long id);
}
