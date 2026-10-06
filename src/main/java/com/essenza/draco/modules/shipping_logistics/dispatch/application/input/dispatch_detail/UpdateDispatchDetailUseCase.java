package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_detail;

import com.essenza.draco.modules.shipping_logistics.dispatch.application.dto.dispatch_detail.DispatchDetailDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.dto.dispatch_detail.UpdateDispatchDetailDto;

public interface UpdateDispatchDetailUseCase {
    DispatchDetailDto update(Long id, UpdateDispatchDetailDto input);
}
