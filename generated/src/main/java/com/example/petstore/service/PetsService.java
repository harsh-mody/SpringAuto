package com.example.petstore.service;

import com.example.petstore.model.CreatePetRequest;
import com.example.petstore.model.Pet;
import com.example.petstore.model.UpdatePetRequest;
import java.util.List;

public interface PetsService {

    List<Pet> listPets(String status, Integer page, Integer size);
    Pet createPet(CreatePetRequest request);
    Pet getPetById(Long petId);
    Pet updatePet(Long petId, UpdatePetRequest request);
    void deletePet(Long petId);
}
