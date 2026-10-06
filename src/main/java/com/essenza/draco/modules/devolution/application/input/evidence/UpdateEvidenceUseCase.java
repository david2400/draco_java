package com.essenza.draco.modules.devolution.application.input.evidence;

import com.essenza.draco.modules.devolution.application.dto.evidence.EvidenceDto;
import com.essenza.draco.modules.devolution.application.dto.evidence.UpdateEvidenceDto;

public interface UpdateEvidenceUseCase {
    EvidenceDto update(Long id, UpdateEvidenceDto input);
}
