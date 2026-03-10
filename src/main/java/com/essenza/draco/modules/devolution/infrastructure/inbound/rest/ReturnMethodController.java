package com.essenza.draco.modules.devolution.infrastructure.inbound.rest;

import com.essenza.draco.modules.devolution.application.input.return_method.*;
import com.essenza.draco.modules.devolution.domain.dto.return_method.CreateReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.ReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.UpdateReturnMethodDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/devolution/return_methods")
@Tag(name = "Return Methods")
public class ReturnMethodController {

    private final CreateReturnMethodUseCase createReturnMethod;
    private final UpdateReturnMethodUseCase updateReturnMethod;
    private final DeleteReturnMethodUseCase deleteReturnMethod;
    private final FindReturnMethodByIdUseCase findReturnMethodById;
    private final FindReturnMethodsUseCase findReturnMethods;

    public ReturnMethodController(CreateReturnMethodUseCase createReturnMethod,
                                  UpdateReturnMethodUseCase updateReturnMethod,
                                  DeleteReturnMethodUseCase deleteReturnMethod,
                                  FindReturnMethodByIdUseCase findReturnMethodById,
                                  FindReturnMethodsUseCase findReturnMethods) {
        this.createReturnMethod = createReturnMethod;
        this.updateReturnMethod = updateReturnMethod;
        this.deleteReturnMethod = deleteReturnMethod;
        this.findReturnMethodById = findReturnMethodById;
        this.findReturnMethods = findReturnMethods;
    }

    @Operation(summary = "Create return method", description = "Creates a new return method and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Return method created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnMethodDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ReturnMethodDto> create(
            @RequestBody(description = "Return method to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateReturnMethodDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateReturnMethodDto input) {
        ReturnMethodDto created = createReturnMethod.create(input);
        return ResponseEntity.created(URI.create("/return-methods/" + created.getId())).body(created);
    }

    @Operation(summary = "Update return method", description = "Updates an existing return method by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return method updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnMethodDto.class))),
            @ApiResponse(responseCode = "404", description = "Return method not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReturnMethodDto> update(
            @Parameter(description = "Return method ID", required = true) @PathVariable Long id,
            @RequestBody(description = "Return method data to update", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateReturnMethodDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateReturnMethodDto input) {
        ReturnMethodDto updated = updateReturnMethod.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get return method by ID", description = "Returns a return method by its ID or 404 if not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return method found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnMethodDto.class))),
            @ApiResponse(responseCode = "404", description = "Return method not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReturnMethodDto> getById(@Parameter(description = "Return method ID", required = true) @PathVariable Long id) {
        Optional<ReturnMethodDto> result = findReturnMethodById.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List return methods", description = "Returns all return methods")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of return methods",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<ReturnMethodDto> getAll() {
        return findReturnMethods.findAll();
    }

    @Operation(summary = "Delete return method", description = "Deletes a return method by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Return method deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Return method not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Return method ID", required = true) @PathVariable Long id) {
        boolean deleted = deleteReturnMethod.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
