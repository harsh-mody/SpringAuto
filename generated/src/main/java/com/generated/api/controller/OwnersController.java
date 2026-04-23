package com.generated.api.controller;

import com.generated.api.dto.ApiResponse;
import com.generated.api.service.OwnersService;
import com.generated.api.model.CreateOwnerRequest;
import com.generated.api.model.Owner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Validated
@Tag(name = "owners")
public class OwnersController {

    private final OwnersService ownersService;

    public OwnersController(OwnersService ownersService) {
        this.ownersService = ownersService;
    }

    @Operation(summary = "List all owners")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of owners"),
    })
    @GetMapping("/owners")
    public ResponseEntity<ApiResponse<List<Owner>>> listOwners(
            @RequestParam(name = "lastName", required = false) String lastName
    ) {
        List<Owner> result = ownersService.listOwners(lastName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(result, 200, "List all owners"));
    }

    @Operation(summary = "Register a new owner")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Owner created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
    })
    @PostMapping("/owners")
    public ResponseEntity<ApiResponse<Owner>> createOwner(
            @Valid @RequestBody CreateOwnerRequest request
    ) {
        Owner result = ownersService.createOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, 201, "Register a new owner"));
    }

    @Operation(summary = "Get an owner by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Owner found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Owner not found"),
    })
    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<ApiResponse<Owner>> getOwnerById(
            @PathVariable(name = "ownerId") Long ownerId
    ) {
        Owner result = ownersService.getOwnerById(ownerId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(result, 200, "Get an owner by ID"));
    }

}
