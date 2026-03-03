package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.tracking;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.tracking.TrackingDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.tracking.UpdateTrackingDto;

public interface UpdateTrackingUseCase {
    TrackingDto update(Long id, UpdateTrackingDto input);
}
