package com.essenza.draco.modules.devolution.domain.dto.return_method;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReturnMethodDto extends CreateReturnMethodDto {
    @NotNull
    @Positive
    private Long id;
}
