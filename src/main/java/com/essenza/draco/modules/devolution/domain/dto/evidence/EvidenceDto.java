package com.essenza.draco.modules.devolution.domain.dto.evidence;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceDto extends AuditInfoDto {
    private Long id;
    private Long orderDevolutionId;
    private String evidenceType;
    private String resourceUrl;
    private String description;
    private String recordedBy;
    private LocalDateTime recordedAt;
}
