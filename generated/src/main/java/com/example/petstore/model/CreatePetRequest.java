package com.example.petstore.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

/** Request body for creating a new pet */
public class CreatePetRequest implements Serializable {

    public enum StatusEnum {
        AVAILABLE,
        PENDING,
        SOLD,
    }

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 '-]+$")
    @Size(min = 1, max = 100)
    private String name;
    @NotBlank
    @Size(min = 1, max = 50)
    private String species;
    @Size(max = 100)
    private String breed;
    @NotNull
    private StatusEnum status;
    private java.time.LocalDate dateOfBirth;
    private Long ownerId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public StatusEnum getStatus() { return status; }
    public void setStatus(StatusEnum status) { this.status = status; }
    public java.time.LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(java.time.LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
