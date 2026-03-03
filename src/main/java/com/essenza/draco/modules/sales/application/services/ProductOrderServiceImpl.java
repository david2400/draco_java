package com.essenza.draco.modules.sales.application.services;

import com.essenza.draco.modules.sales.application.input.product_order.*;
import com.essenza.draco.modules.sales.domain.dto.product_order.CreateProductOrderDto;
import com.essenza.draco.modules.sales.domain.dto.product_order.ProductOrderDto;
import com.essenza.draco.modules.sales.domain.dto.product_order.UpdateProductOrderDto;
import com.essenza.draco.modules.sales.infrastructure.outbound.repositories.product_order.ProductOrderRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductOrderServiceImpl implements CreateProductOrderUseCase,
        UpdateProductOrderUseCase,
        DeleteProductOrderUseCase,
        FindProductOrderByIdUseCase,
        FindProductOrdersUseCase {

    private final ProductOrderRepositoryAdapter productOrderRepository;

    public ProductOrderServiceImpl(ProductOrderRepositoryAdapter productOrderRepository) {
        this.productOrderRepository = productOrderRepository;
    }

    @Override
    public ProductOrderDto create(CreateProductOrderDto input) {
        return productOrderRepository.create(input);
    }


    @Override
    public ProductOrderDto update(Long id, UpdateProductOrderDto input) {
        return productOrderRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
//        if (!paymentTypeRepository.existsById(id)) return false;
//        productOrderRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductOrderDto> findById(Long id) {
        return productOrderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductOrderDto> findAll() {
        return productOrderRepository.findAll();
    }
}
