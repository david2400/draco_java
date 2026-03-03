package com.essenza.draco.modules.devolution.application.input.evidence;

import com.essenza.draco.modules.devolution.domain.dto.evidence.EvidenceDto;

import java.util.List;

public interface FindEvidencesUseCase {
    List<EvidenceDto> findAll();
}
