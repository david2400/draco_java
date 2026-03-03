package com.essenza.draco.modules.shipping_logistics.dispatch.application.output.repository;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.DispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.CreateDispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.UpdateDispatchProductDto;

import java.util.List;
import java.util.Optional;

public interface DispatchProductRepository {

    DispatchProductDto create(CreateDispatchProductDto input);

    DispatchProductDto update(Long id, UpdateDispatchProductDto input);

    boolean deleteById(Long id);

    Optional<DispatchProductDto> findById(Long id);

    List<DispatchProductDto> findAll();
}
