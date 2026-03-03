// package com.essenza.draco.modules.inventory.infrastructure.inbound.rest;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.parameters.RequestBody;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.essenza.draco.modules.inventory.application.input.combo.CreateComboUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.UpdateComboUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.DeleteComboUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.FindComboByIdUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.FindCombosUseCase;
// import com.essenza.draco.modules.inventory.domain.dto.combo.ComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.combo.CreateComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.combo.UpdateComboDto;

// import java.net.URI;
// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/combos")
// public class ComboController {

//     private final CreateComboUseCase createCombo;
//     private final UpdateComboUseCase updateCombo;
//     private final DeleteComboUseCase deleteCombo;
//     private final FindComboByIdUseCase findComboById;
//     private final FindCombosUseCase findCombos;

//     public ComboController(CreateComboUseCase createCombo,
//                            UpdateComboUseCase updateCombo,
//                            DeleteComboUseCase deleteCombo,
//                            FindComboByIdUseCase findComboById,
//                            FindCombosUseCase findCombos) {
//         this.createCombo = createCombo;
//         this.updateCombo = updateCombo;
//         this.deleteCombo = deleteCombo;
//         this.findComboById = findComboById;
//         this.findCombos = findCombos;
//     }

//     @Operation(summary = "Create combo", description = "Creates a new combo and returns it with its generated ID")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "201", description = "Combo created",
//                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComboDto.class))),
//             @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
//             @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
//     })
//     @PostMapping
//     public ResponseEntity<ComboDto> create(
//             @RequestBody(description = "Combo to create", required = true,
//                     content = @Content(schema = @Schema(implementation = CreateComboDto.class)))
//             @org.springframework.web.bind.annotation.RequestBody CreateComboDto input) {
//         ComboDto created = createCombo.create(input);
//         return ResponseEntity.created(URI.create("/combos/" )).body(created);
//     }

//     @Operation(summary = "Update combo", description = "Updates an existing combo by ID")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Combo updated",
//                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComboDto.class))),
//             @ApiResponse(responseCode = "404", description = "Combo not found", content = @Content),
//             @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
//     })
//     @PutMapping("/{id}")
//     public ResponseEntity<ComboDto> update(
//             @Parameter(description = "Combo ID", required = true) @PathVariable Long id,
//             @RequestBody(description = "Combo data to update", required = true,
//                     content = @Content(schema = @Schema(implementation = UpdateComboDto.class)))
//             @org.springframework.web.bind.annotation.RequestBody UpdateComboDto input) {
//         ComboDto updated = updateCombo.update(id, input);
//         return ResponseEntity.ok(updated);
//     }

//     @Operation(summary = "Get combo by ID", description = "Returns a combo by its ID or 404 if not found")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Combo found",
//                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComboDto.class))),
//             @ApiResponse(responseCode = "404", description = "Combo not found", content = @Content)
//     })
//     @GetMapping("/{id}")
//     public ResponseEntity<ComboDto> getById(@Parameter(description = "Combo ID", required = true) @PathVariable Long id) {
//         Optional<ComboDto> result = findComboById.findById(id);
//         return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
//     }

//     @Operation(summary = "List combos", description = "Returns all combos")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "List of combos",
//                     content = @Content(mediaType = "application/json"))
//     })
//     @GetMapping
//     public List<ComboDto> getAll() {
//         return findCombos.findAll();
//     }

//     @Operation(summary = "Delete combo", description = "Deletes a combo by ID")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "204", description = "Combo deleted", content = @Content),
//             @ApiResponse(responseCode = "404", description = "Combo not found", content = @Content)
//     })
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> delete(@Parameter(description = "Combo ID", required = true) @PathVariable Long id) {
//         Boolean deleted = deleteCombo.deleteById(id);
//         return Boolean.TRUE.equals(deleted) ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//     }
// }
