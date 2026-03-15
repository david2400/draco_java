package com.essenza.draco.modules.advanced_features.infrastructure.inbound.rest;

import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.CreatePersonalizationProfileUseCase;
import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.DeletePersonalizationProfileUseCase;
import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.FindPersonalizationProfileByIdUseCase;
import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.FindPersonalizationProfilesUseCase;
import com.essenza.draco.modules.advanced_features.application.input.personalization_profile.UpdatePersonalizationProfileUseCase;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.CreatePersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.PersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.UpdatePersonalizationProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.Optional;

@RestController
@RequestMapping("/advanced-features/personalization-profiles")
@Tag(name = "Personalization Profiles", description = "Advanced personalization profile CRUD operations")
public class PersonalizationProfileController {

    private final CreatePersonalizationProfileUseCase createUseCase;
    private final UpdatePersonalizationProfileUseCase updateUseCase;
    private final DeletePersonalizationProfileUseCase deleteUseCase;
    private final FindPersonalizationProfileByIdUseCase findByIdUseCase;
    private final FindPersonalizationProfilesUseCase findAllUseCase;

    public PersonalizationProfileController(CreatePersonalizationProfileUseCase createUseCase,
                                            UpdatePersonalizationProfileUseCase updateUseCase,
                                            DeletePersonalizationProfileUseCase deleteUseCase,
                                            FindPersonalizationProfileByIdUseCase findByIdUseCase,
                                            FindPersonalizationProfilesUseCase findAllUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.findByIdUseCase = findByIdUseCase;
        this.findAllUseCase = findAllUseCase;
    }

    @Operation(summary = "Create personalization profile", description = "Creates a personalization profile entry")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonalizationProfileDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payload", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PersonalizationProfileDto> create(@Valid @RequestBody CreatePersonalizationProfileDto input) {
        PersonalizationProfileDto created = createUseCase.create(input);
        return ResponseEntity.created(URI.create("/advanced-features/personalization-profiles/" + created.getId()))
                .body(created);
    }

    @Operation(summary = "Update personalization profile", description = "Updates an existing profile by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonalizationProfileDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "Profile not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PersonalizationProfileDto> update(
            @Parameter(description = "Profile id", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdatePersonalizationProfileDto input) {
        PersonalizationProfileDto updated = updateUseCase.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Find personalization profile", description = "Retrieves a profile by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonalizationProfileDto.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PersonalizationProfileDto> findById(
            @Parameter(description = "Profile id", required = true) @PathVariable Long id) {
        Optional<PersonalizationProfileDto> dto = findByIdUseCase.findById(id);
        return dto.map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List personalization profiles", description = "Returns all personalization profiles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profiles retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonalizationProfileDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<PersonalizationProfileDto>> findAll() {
        List<PersonalizationProfileDto> profiles = findAllUseCase.findAll();
        return ResponseEntity.ok(profiles);
    }

    @Operation(summary = "Delete personalization profile", description = "Deletes a profile by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profile deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Profile not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Profile id", required = true) @PathVariable Long id) {
        boolean deleted = deleteUseCase.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
