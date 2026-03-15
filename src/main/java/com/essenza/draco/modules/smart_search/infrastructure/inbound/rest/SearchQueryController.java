package com.essenza.draco.modules.smart_search.infrastructure.inbound.rest;

import com.essenza.draco.modules.smart_search.application.input.search_query.CreateSearchQueryUseCase;
import com.essenza.draco.modules.smart_search.application.input.search_query.DeleteSearchQueryUseCase;
import com.essenza.draco.modules.smart_search.application.input.search_query.FindSearchQueriesUseCase;
import com.essenza.draco.modules.smart_search.application.input.search_query.FindSearchQueryByIdUseCase;
import com.essenza.draco.modules.smart_search.application.input.search_query.UpdateSearchQueryUseCase;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.CreateSearchQueryDto;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.SearchQueryDto;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.UpdateSearchQueryDto;
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
@RequestMapping("/smart-search/queries")
@Validated
@Tag(name = "Smart Search Queries")
public class SearchQueryController {

    private final CreateSearchQueryUseCase createSearchQuery;
    private final UpdateSearchQueryUseCase updateSearchQuery;
    private final DeleteSearchQueryUseCase deleteSearchQuery;
    private final FindSearchQueryByIdUseCase findSearchQueryById;
    private final FindSearchQueriesUseCase findSearchQueries;

    public SearchQueryController(CreateSearchQueryUseCase createSearchQuery,
                                 UpdateSearchQueryUseCase updateSearchQuery,
                                 DeleteSearchQueryUseCase deleteSearchQuery,
                                 FindSearchQueryByIdUseCase findSearchQueryById,
                                 FindSearchQueriesUseCase findSearchQueries) {
        this.createSearchQuery = createSearchQuery;
        this.updateSearchQuery = updateSearchQuery;
        this.deleteSearchQuery = deleteSearchQuery;
        this.findSearchQueryById = findSearchQueryById;
        this.findSearchQueries = findSearchQueries;
    }

    @Operation(summary = "Create search query")
    @PostMapping
    public ResponseEntity<SearchQueryDto> create(@Valid @RequestBody CreateSearchQueryDto input) {
        SearchQueryDto created = createSearchQuery.create(input);
        return ResponseEntity.created(URI.create("/smart-search/queries/" + created.getId())).body(created);
    }

    @Operation(summary = "Update search query")
    @PutMapping("/{id}")
    public ResponseEntity<SearchQueryDto> update(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateSearchQueryDto input) {
        SearchQueryDto updated = updateSearchQuery.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get search query by id")
    @GetMapping("/{id}")
    public ResponseEntity<SearchQueryDto> getById(@PathVariable Long id) {
        return findSearchQueryById.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List search queries")
    @GetMapping
    public List<SearchQueryDto> list() {
        return findSearchQueries.findAll();
    }

    @Operation(summary = "Delete search query")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = deleteSearchQuery.deleteById(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }
}
