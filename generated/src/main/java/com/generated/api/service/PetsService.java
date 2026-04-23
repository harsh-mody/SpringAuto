package com.generated.api.service;

import com.generated.api.model.CreatePetRequest;
import com.generated.api.model.Pet;
import com.generated.api.model.UpdatePetRequest;
import java.util.List;

public interface PetsService {

    List<Pet> listPets(String status, Integer page, Integer size);
    Pet createPet(CreatePetRequest request);
    Pet getPetById(Long petId);
    Pet updatePet(Long petId, UpdatePetRequest request);
    void deletePet(Long petId);
}
