package com.example.petstore.controller;

import com.example.petstore.dto.ApiResponse;
import com.example.petstore.service.PetsService;
import com.example.petstore.model.CreatePetRequest;
import com.example.petstore.model.Pet;
import com.example.petstore.model.UpdatePetRequest;
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
@Tag(name = "pets")
public class PetsController {

    private final PetsService petsService;

    public PetsController(PetsService petsService) {
        this.petsService = petsService;
    }

    @Operation(summary = "List all pets")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successful response"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Too many requests"),
    })
    @GetMapping("/pets")
    public ResponseEntity<ApiResponse<List<Pet>>> listPets(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        List<Pet> result = petsService.listPets(status, page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(result, 200, "List all pets"));
    }

    @Operation(summary = "Create a new pet")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Pet created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Pet already exists"),
    })
    @PostMapping("/pets")
    public ResponseEntity<ApiResponse<Pet>> createPet(
            @Valid @RequestBody CreatePetRequest request
    ) {
        Pet result = petsService.createPet(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, 201, "Create a new pet"));
    }

    @Operation(summary = "Get a pet by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pet found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pet not found"),
    })
    @GetMapping("/pets/{petId}")
    public ResponseEntity<ApiResponse<Pet>> getPetById(
            @PathVariable(name = "petId") Long petId
    ) {
        Pet result = petsService.getPetById(petId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(result, 200, "Get a pet by ID"));
    }

    @Operation(summary = "Update a pet")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pet updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pet not found"),
    })
    @PutMapping("/pets/{petId}")
    public ResponseEntity<ApiResponse<Pet>> updatePet(
            @PathVariable(name = "petId") Long petId,
            @Valid @RequestBody UpdatePetRequest request
    ) {
        Pet result = petsService.updatePet(petId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(result, 200, "Update a pet"));
    }

    @Operation(summary = "Delete a pet")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Pet deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pet not found"),
    })
    @DeleteMapping("/pets/{petId}")
    public ResponseEntity<ApiResponse<Void>> deletePet(
            @PathVariable(name = "petId") Long petId
    ) {
        petsService.deletePet(petId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, 204, "Delete a pet"));
    }

}
