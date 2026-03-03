package com.essenza.draco.modules.promotions.infrastructure.inbound.rest;

import com.essenza.draco.modules.promotions.application.input.coupon.*;
import com.essenza.draco.modules.promotions.domain.dto.coupon.CouponDto;
import com.essenza.draco.modules.promotions.domain.dto.coupon.CreateCouponDto;
import com.essenza.draco.modules.promotions.domain.dto.coupon.UpdateCouponDto;
import com.essenza.draco.modules.promotions.application.services.CouponValidationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/coupons")
@Tag(name = "Coupons", description = "Coupon management operations")
public class CouponController {

    private final CreateCouponUseCase createCoupon;
    private final UpdateCouponUseCase updateCoupon;
    private final DeleteCouponUseCase deleteCoupon;
    private final FindCouponByIdUseCase findCouponById;
    private final FindCouponsUseCase findCoupons;
    private final ValidateCouponUseCase validateCoupon;
    private final CouponValidationService couponValidationService;

    public CouponController(CreateCouponUseCase createCoupon,
                           UpdateCouponUseCase updateCoupon,
                           DeleteCouponUseCase deleteCoupon,
                           FindCouponByIdUseCase findCouponById,
                           FindCouponsUseCase findCoupons,
                           ValidateCouponUseCase validateCoupon,
                           CouponValidationService couponValidationService) {
        this.createCoupon = createCoupon;
        this.updateCoupon = updateCoupon;
        this.deleteCoupon = deleteCoupon;
        this.findCouponById = findCouponById;
        this.findCoupons = findCoupons;
        this.validateCoupon = validateCoupon;
        this.couponValidationService = couponValidationService;
    }

    @Operation(summary = "Create coupon", description = "Creates a new coupon and returns it with its generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CouponDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Coupon code already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CouponDto> create(@Valid @RequestBody CreateCouponDto createCouponDto) {
        CouponDto created = createCoupon.create(createCouponDto);
        return ResponseEntity.created(URI.create("/coupons/" + created.getId())).body(created);
    }

    @Operation(summary = "Update coupon", description = "Updates an existing coupon by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CouponDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CouponDto> update(@Parameter(description = "Coupon ID", required = true) @PathVariable Long id,
                                           @Valid @RequestBody UpdateCouponDto updateCouponDto) {
        CouponDto updated = updateCoupon.update(id, updateCouponDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get coupon by ID", description = "Retrieves a coupon by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CouponDto.class))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CouponDto> findById(@Parameter(description = "Coupon ID", required = true) @PathVariable Long id) {
        Optional<CouponDto> coupon = findCouponById.findById(id);
        return coupon.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all coupons", description = "Retrieves all coupons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupons retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CouponDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<CouponDto>> findAll() {
        List<CouponDto> coupons = findCoupons.findAll();
        return ResponseEntity.ok(coupons);
    }

    @Operation(summary = "Delete coupon", description = "Deletes a coupon by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Coupon deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Coupon ID", required = true) @PathVariable Long id) {
        boolean deleted = deleteCoupon.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Validate coupon", description = "Validates a coupon code for a specific order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon is valid",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CouponDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid coupon or not applicable", content = @Content),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content)
    })
    @PostMapping("/validate")
    public ResponseEntity<CouponDto> validateCoupon(
            @Parameter(description = "Coupon code", required = true) @RequestParam String code,
            @Parameter(description = "Order amount", required = true) @RequestParam BigDecimal orderAmount,
            @Parameter(description = "Product IDs") @RequestParam(required = false) List<Long> productIds,
            @Parameter(description = "Category IDs") @RequestParam(required = false) List<Long> categoryIds) {
        
        Optional<CouponDto> validCoupon = validateCoupon.validateCoupon(code, orderAmount, 
                                                                       productIds != null ? productIds : List.of(),
                                                                       categoryIds != null ? categoryIds : List.of());
        return validCoupon.map(ResponseEntity::ok).orElse(ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Calculate discount", description = "Calculates the discount amount for a coupon and order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discount calculated"),
            @ApiResponse(responseCode = "400", description = "Invalid coupon", content = @Content)
    })
    @PostMapping("/calculate-discount")
    public ResponseEntity<BigDecimal> calculateDiscount(
            @Parameter(description = "Coupon code", required = true) @RequestParam String code,
            @Parameter(description = "Order amount", required = true) @RequestParam BigDecimal orderAmount,
            @Parameter(description = "Product IDs") @RequestParam(required = false) List<Long> productIds,
            @Parameter(description = "Category IDs") @RequestParam(required = false) List<Long> categoryIds) {
        
        Optional<CouponDto> validCoupon = validateCoupon.validateCoupon(code, orderAmount,
                                                                       productIds != null ? productIds : List.of(),
                                                                       categoryIds != null ? categoryIds : List.of());
        
        if (validCoupon.isPresent()) {
            BigDecimal discount = couponValidationService.calculateDiscount(validCoupon.get(), orderAmount);
            return ResponseEntity.ok(discount);
        }
        
        return ResponseEntity.badRequest().build();
    }
}
