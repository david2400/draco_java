package com.essenza.draco.modules.devolution.application.input.evidence;

import com.essenza.draco.modules.devolution.application.dto.evidence.CreateEvidenceDto;
import com.essenza.draco.modules.devolution.application.dto.evidence.EvidenceDto;

public interface CreateEvidenceUseCase {
    EvidenceDto create(CreateEvidenceDto input);
}
