package com.generated.api.service;

import com.generated.api.model.CreateOwnerRequest;
import com.generated.api.model.Owner;
import java.util.List;

public interface OwnersService {

    List<Owner> listOwners(String lastName);
    Owner createOwner(CreateOwnerRequest request);
    Owner getOwnerById(Long ownerId);
}
