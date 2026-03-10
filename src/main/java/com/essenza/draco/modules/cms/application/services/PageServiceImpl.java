package com.essenza.draco.modules.cms.application.services;

import com.essenza.draco.modules.cms.application.input.page.CreatePageUseCase;
import com.essenza.draco.modules.cms.application.input.page.DeletePageUseCase;
import com.essenza.draco.modules.cms.application.input.page.FindPageByIdUseCase;
import com.essenza.draco.modules.cms.application.input.page.FindPagesUseCase;
import com.essenza.draco.modules.cms.application.input.page.UpdatePageUseCase;
import com.essenza.draco.modules.cms.application.output.repository.PageRepository;
import com.essenza.draco.modules.cms.domain.dto.page.CreatePageDto;
import com.essenza.draco.modules.cms.domain.dto.page.PageDto;
import com.essenza.draco.modules.cms.domain.dto.page.UpdatePageDto;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class PageServiceImpl implements CreatePageUseCase,
        UpdatePageUseCase,
        DeletePageUseCase,
        FindPageByIdUseCase,
        FindPagesUseCase {

    private final PageRepository repository;

    public PageServiceImpl(PageRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageDto create(CreatePageDto input) {
        String slug = resolveSlug(input.getSlug(), input.getTitle());
        ensureSlugIsUnique(slug, null);
        input.setSlug(slug);

        if (input.getStatus() == null || input.getStatus().isBlank()) {
            input.setStatus("DRAFT");
        }

        return repository.create(input);
    }

    @Override
    public PageDto update(Long id, UpdatePageDto input) {
        repository.findById(id).orElseThrow(() -> new NotFoundException("Page not found: " + id));

        if (input.getSlug() != null && !input.getSlug().isBlank()) {
            String slug = slugify(input.getSlug());
            ensureSlugIsUnique(slug, id);
            input.setSlug(slug);
        } else if (input.getTitle() != null && !input.getTitle().isBlank()) {
            // allow regenerating slug from new title when slug not provided
            String slug = slugify(input.getTitle());
            ensureSlugIsUnique(slug, id);
            input.setSlug(slug);
        }

        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PageDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PageDto> findAll() {
        return repository.findAll();
    }

    private void ensureSlugIsUnique(String slug, Long excludeId) {
        boolean exists = (excludeId == null)
                ? repository.existsBySlug(slug)
                : repository.existsBySlugExcludingId(slug, excludeId);

        if (exists) {
            throw new IllegalArgumentException("Slug already in use: " + slug);
        }
    }

    private String resolveSlug(String slugCandidate, String fallbackTitle) {
        if (slugCandidate != null && !slugCandidate.isBlank()) {
            return slugify(slugCandidate);
        }
        if (fallbackTitle == null || fallbackTitle.isBlank()) {
            throw new IllegalArgumentException("Title or slug must be provided");
        }
        return slugify(fallbackTitle);
    }

    private String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", " ")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Unable to generate slug from input");
        }

        return normalized;
    }
}
