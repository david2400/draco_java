package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_detail;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_detail.CreateDispatchDetailDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_detail.DispatchDetailDto;

public interface CreateDispatchDetailUseCase {
    DispatchDetailDto create(CreateDispatchDetailDto input);
}
