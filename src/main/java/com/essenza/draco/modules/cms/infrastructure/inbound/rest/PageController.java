package com.essenza.draco.modules.cms.infrastructure.inbound.rest;

import com.essenza.draco.modules.cms.application.input.page.CreatePageUseCase;
import com.essenza.draco.modules.cms.application.input.page.DeletePageUseCase;
import com.essenza.draco.modules.cms.application.input.page.FindPageByIdUseCase;
import com.essenza.draco.modules.cms.application.input.page.FindPagesUseCase;
import com.essenza.draco.modules.cms.application.input.page.UpdatePageUseCase;
import com.essenza.draco.modules.cms.domain.dto.page.CreatePageDto;
import com.essenza.draco.modules.cms.domain.dto.page.PageDto;
import com.essenza.draco.modules.cms.domain.dto.page.UpdatePageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cms/pages")
@Validated
@Tag(name = "CMS Pages")
public class PageController {

    private final CreatePageUseCase createPage;
    private final UpdatePageUseCase updatePage;
    private final DeletePageUseCase deletePage;
    private final FindPageByIdUseCase findPageById;
    private final FindPagesUseCase findPages;

    public PageController(CreatePageUseCase createPage,
                          UpdatePageUseCase updatePage,
                          DeletePageUseCase deletePage,
                          FindPageByIdUseCase findPageById,
                          FindPagesUseCase findPages) {
        this.createPage = createPage;
        this.updatePage = updatePage;
        this.deletePage = deletePage;
        this.findPageById = findPageById;
        this.findPages = findPages;
    }

    @Operation(summary = "Create CMS page")
    @PostMapping
    public ResponseEntity<PageDto> create(@Valid @RequestBody CreatePageDto input) {
        PageDto created = createPage.create(input);
        return ResponseEntity.created(URI.create("/cms/pages/" + created.getId())).body(created);
    }

    @Operation(summary = "Update CMS page")
    @PutMapping("/{id}")
    public ResponseEntity<PageDto> update(@PathVariable Long id,
                                          @Valid @RequestBody UpdatePageDto input) {
        PageDto updated = updatePage.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get page by id")
    @GetMapping("/{id}")
    public ResponseEntity<PageDto> getById(@PathVariable Long id) {
        return findPageById.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List pages")
    @GetMapping
    public List<PageDto> getAll() {
        return findPages.findAll();
    }

    @Operation(summary = "Delete page")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = deletePage.deleteById(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }
}
