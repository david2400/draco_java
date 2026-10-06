package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.tracking;

import com.essenza.draco.modules.shipping_logistics.dispatch.application.dto.tracking.TrackingDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.dto.tracking.UpdateTrackingDto;

public interface UpdateTrackingUseCase {
    TrackingDto update(Long id, UpdateTrackingDto input);
}
