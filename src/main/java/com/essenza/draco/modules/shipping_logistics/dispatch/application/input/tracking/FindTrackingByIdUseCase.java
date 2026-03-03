package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.tracking;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.tracking.TrackingDto;

import java.util.Optional;

public interface FindTrackingByIdUseCase {
    Optional<TrackingDto> findById(Long id);
}
