package com.example.petstore.soap;

import com.example.petstore.service.PetsService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class PetsSoapEndpoint {

    private static final String NAMESPACE_URI = "http://com.example.petstore/ws/pets";
    private final PetsService petsService;

    public PetsSoapEndpoint(PetsService petsService) {
        this.petsService = petsService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ListPetsRequest")
    @ResponsePayload
    public ListPetsSoapResponse listPets(@RequestPayload ListPetsSoapRequest request) {
        ListPetsSoapResponse response = new ListPetsSoapResponse();
        response.setResult(petsService.listPets(request.getStatus(), request.getPage(), request.getSize()));
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreatePetRequest")
    @ResponsePayload
    public CreatePetSoapResponse createPet(@RequestPayload CreatePetSoapRequest request) {
        CreatePetSoapResponse response = new CreatePetSoapResponse();
        response.setResult(petsService.createPet(request.getBody()));
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetPetByIdRequest")
    @ResponsePayload
    public GetPetByIdSoapResponse getPetById(@RequestPayload GetPetByIdSoapRequest request) {
        GetPetByIdSoapResponse response = new GetPetByIdSoapResponse();
        response.setResult(petsService.getPetById(request.getPetId()));
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdatePetRequest")
    @ResponsePayload
    public UpdatePetSoapResponse updatePet(@RequestPayload UpdatePetSoapRequest request) {
        UpdatePetSoapResponse response = new UpdatePetSoapResponse();
        response.setResult(petsService.updatePet(request.getPetId(), request.getBody()));
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeletePetRequest")
    @ResponsePayload
    public DeletePetSoapResponse deletePet(@RequestPayload DeletePetSoapRequest request) {
        DeletePetSoapResponse response = new DeletePetSoapResponse();
        petsService.deletePet(request.getPetId());
        response.setSuccess(true);
        return response;
    }

}
