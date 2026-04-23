package com.generated.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/** Represents a pet in the store */
public class Pet implements Serializable {

    public enum StatusEnum {
        AVAILABLE,
        PENDING,
        SOLD,
    }

    @NotNull
    private Long id;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 '-]+$")
    @Size(min = 1, max = 100)
    private String name;
    @Size(max = 50)
    private String species;
    @Size(max = 100)
    private String breed;
    @NotNull
    private StatusEnum status;
    private java.time.LocalDate dateOfBirth;
    private Long ownerId;
    @Size(max = 10)
    private List<String> tags;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
