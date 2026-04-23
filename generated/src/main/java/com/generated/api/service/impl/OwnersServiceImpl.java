package com.generated.api.service.impl;

import com.generated.api.dto.ApiException;
import com.generated.api.service.OwnersService;
import com.generated.api.model.CreateOwnerRequest;
import com.generated.api.model.Owner;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class OwnersServiceImpl implements OwnersService {

    private final Map<Long, Owner> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1);

    @Override
    public List<Owner> listOwners(String lastName) {
        List<Owner> result = new ArrayList<>(store.values());
        if (lastName != null && !lastName.isBlank()) {
            result = result.stream()
                    .filter(e -> e.getLastName() != null && e.getLastName().toString().equalsIgnoreCase(lastName))
                    .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public Owner createOwner(CreateOwnerRequest request) {
        Owner entity = new Owner();
        entity.setId(idSeq.getAndIncrement());
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Owner getOwnerById(Long ownerId) {
        Owner entity = store.get(ownerId);
        if (entity == null)
            throw ApiException.notFound("Owner not found with id: " + ownerId);
        return entity;
    }

}
