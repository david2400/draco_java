package com.essenza.draco.modules.catalog.application.services;

import com.essenza.draco.modules.catalog.application.input.subcategory.*;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.UpdateSubcategoryDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.subcategory.SubcategoryRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubcategoryServiceImpl implements CreateSubcategoryUseCase, UpdateSubcategoryUseCase, DeleteSubcategoryByIdUseCase, FindAllSubcategoriesUseCase, FindSubcategoryByIdUseCase {

    private final SubcategoryRepositoryAdapter subcategoryRepository;

    public SubcategoryServiceImpl(SubcategoryRepositoryAdapter subcategoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
    }

//    public SubcategoryServiceImpl(JpaSubcategoryRepository subcategoryRepository) {
//        this.subcategoryRepository = subcategoryRepository;
//    }

    @Override
    public SubcategoryDto create(CreateSubcategoryDto input) {
//        SubcategoryEntity entity = SubcategoryEntity.builder()
//                .name(input.getName())
//                .slug(input.getSlug())
//                .categoryId(input.getCategoryId())
//                .build();
        return subcategoryRepository.create(input);
    }

    @Override
    public SubcategoryDto update(Long id, UpdateSubcategoryDto input) {
//        Optional<SubcategoryEntity> existing = subcategoryRepository.findById(id);
//        if (existing.isEmpty()) return false;
//        SubcategoryEntity updated = SubcategoryEntity.builder()
//                .id(id)
//                .name(input.getName())
//                .slug(input.getSlug())
//                .categoryId(input.getCategoryId())
//                .build();
//        subcategoryRepository.save(updated);
        return subcategoryRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return subcategoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubcategoryDto> findById(Long id) {
        return subcategoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubcategoryDto> findAll() {
        return subcategoryRepository.findAll();
    }
}
