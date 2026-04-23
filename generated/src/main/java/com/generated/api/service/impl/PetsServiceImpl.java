package com.generated.api.service.impl;

import com.generated.api.dto.ApiException;
import com.generated.api.service.PetsService;
import com.generated.api.model.CreatePetRequest;
import com.generated.api.model.Pet;
import com.generated.api.model.UpdatePetRequest;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PetsServiceImpl implements PetsService {

    private final Map<Long, Pet> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1);

    @Override
    public List<Pet> listPets(String status, Integer page, Integer size) {
        List<Pet> result = new ArrayList<>(store.values());
        if (status != null && !status.isBlank()) {
            result = result.stream()
                    .filter(e -> e.getStatus() != null && e.getStatus().toString().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        int pageNum  = (page != null && page > 0) ? page : 0;
        int pageSize = (size != null) ? size : 20;
        int from = pageNum * pageSize;
        int to   = Math.min(from + pageSize, result.size());
        return from >= result.size() ? List.of() : result.subList(from, to);
    }

    @Override
    public Pet createPet(CreatePetRequest request) {
        Pet entity = new Pet();
        entity.setId(idSeq.getAndIncrement());
        entity.setName(request.getName());
        entity.setSpecies(request.getSpecies());
        entity.setBreed(request.getBreed());
        if (request.getStatus() != null)
            entity.setStatus(Pet.StatusEnum.valueOf(request.getStatus().name()));
        entity.setDateOfBirth(request.getDateOfBirth());
        entity.setOwnerId(request.getOwnerId());
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Pet getPetById(Long petId) {
        Pet entity = store.get(petId);
        if (entity == null)
            throw ApiException.notFound("Pet not found with id: " + petId);
        return entity;
    }

    @Override
    public Pet updatePet(Long petId, UpdatePetRequest request) {
        Pet entity = store.get(petId);
        if (entity == null)
            throw ApiException.notFound("Pet not found with id: " + petId);
        if (request.getName() != null)
            entity.setName(request.getName());
        if (request.getSpecies() != null)
            entity.setSpecies(request.getSpecies());
        if (request.getBreed() != null)
            entity.setBreed(request.getBreed());
        if (request.getStatus() != null)
            entity.setStatus(Pet.StatusEnum.valueOf(request.getStatus().name()));
        if (request.getOwnerId() != null)
            entity.setOwnerId(request.getOwnerId());
        return entity;
    }

    @Override
    public void deletePet(Long petId) {
        if (!store.containsKey(petId))
            throw ApiException.notFound("Entity not found with id: " + petId);
        store.remove(petId);
    }

}
