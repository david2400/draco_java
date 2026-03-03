package com.essenza.draco.modules.inventory.domain.dto.supplier;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto extends AuditInfoDto {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 30)
    private String phone;
    private String address;
    @Email
    private String email;
}
