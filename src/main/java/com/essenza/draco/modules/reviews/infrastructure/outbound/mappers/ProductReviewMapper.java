package com.essenza.draco.modules.reviews.infrastructure.outbound.mappers;

import com.essenza.draco.modules.reviews.domain.dto.product_review.CreateProductReviewDto;
import com.essenza.draco.modules.reviews.domain.dto.product_review.ProductReviewDto;
import com.essenza.draco.modules.reviews.domain.dto.product_review.UpdateProductReviewDto;
import com.essenza.draco.modules.reviews.infrastructure.outbound.persistence.mysql.shop.ProductReviewEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductReviewMapper {

    ProductReviewDto toDto(ProductReviewEntity entity);

    List<ProductReviewDto> toDtoList(List<ProductReviewEntity> entities);

    ProductReviewEntity toEntity(CreateProductReviewDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateProductReviewDto dto, @MappingTarget ProductReviewEntity entity);
}
