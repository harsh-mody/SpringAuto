package com.example.petstore.model;

import jakarta.validation.constraints.Size;
import java.io.Serializable;

/** Request body for updating a pet */
public class UpdatePetRequest implements Serializable {

    public enum StatusEnum {
        AVAILABLE,
        PENDING,
        SOLD,
    }

    @Size(min = 1, max = 100)
    private String name;
    @Size(max = 50)
    private String species;
    @Size(max = 100)
    private String breed;
    private StatusEnum status;
    private Long ownerId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public StatusEnum getStatus() { return status; }
    public void setStatus(StatusEnum status) { this.status = status; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
