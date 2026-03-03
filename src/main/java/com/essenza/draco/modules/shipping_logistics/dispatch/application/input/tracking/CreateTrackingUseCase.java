package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.tracking;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.tracking.CreateTrackingDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.tracking.TrackingDto;

public interface CreateTrackingUseCase {
    TrackingDto create(CreateTrackingDto input);
}
