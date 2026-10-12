package com.essenza.draco.modules.catalog.application.services.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.essenza.draco.modules.catalog.application.dto.product.ProductImageInputDto;
import com.essenza.draco.modules.catalog.application.dto.product.ReplaceProductImagesDto;
import com.essenza.draco.modules.catalog.application.output.repository.ProductGalleryRepository;
import com.essenza.draco.shared.exceptions.NotFoundException;

class ManageProductImagesServiceTest {

    @Test
    @SuppressWarnings("unchecked")
    void galleryDeduplicatesAndTrims() {
        ProductGalleryRepository repo = mock(ProductGalleryRepository.class);
        when(repo.productExists(5L)).thenReturn(true);
        ManageProductImagesService service = new ManageProductImagesService(repo);

        service.replace(5L, new ReplaceProductImagesDto(List.of(
                new ProductImageInputDto(" https://a/1.jpg ", " Frente "),
                new ProductImageInputDto("https://a/2.jpg", ""),
                new ProductImageInputDto("https://a/1.jpg", "dup"))));

        ArgumentCaptor<List<ProductImageInputDto>> saved = ArgumentCaptor.forClass(List.class);
        verify(repo).replaceGallery(eq(5L), saved.capture());
        assertThat(saved.getValue()).containsExactly(
                new ProductImageInputDto("https://a/1.jpg", "Frente"),
                new ProductImageInputDto("https://a/2.jpg", null));
    }

    @Test
    void galleryOfMissingProductIs404() {
        ProductGalleryRepository repo = mock(ProductGalleryRepository.class);
        ManageProductImagesService service = new ManageProductImagesService(repo);
        assertThatThrownBy(() -> service.list(99L)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> service.replace(99L, new ReplaceProductImagesDto(List.of())))
                .isInstanceOf(NotFoundException.class);
        verify(repo, never()).replaceGallery(any(), anyList());
    }
}
