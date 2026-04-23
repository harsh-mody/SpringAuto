package com.example.petstore.service;

import com.example.petstore.model.CreateOwnerRequest;
import com.example.petstore.model.Owner;
import java.util.List;

public interface OwnersService {

    List<Owner> listOwners(String lastName);
    Owner createOwner(CreateOwnerRequest request);
    Owner getOwnerById(Long ownerId);
}
