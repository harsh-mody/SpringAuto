package com.example.petstore.soap;

import com.example.petstore.service.OwnersService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class OwnersSoapEndpoint {

    private static final String NAMESPACE_URI = "http://com.example.petstore/ws/owners";
    private final OwnersService ownersService;

    public OwnersSoapEndpoint(OwnersService ownersService) {
        this.ownersService = ownersService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ListOwnersRequest")
    @ResponsePayload
    public ListOwnersSoapResponse listOwners(@RequestPayload ListOwnersSoapRequest request) {
        ListOwnersSoapResponse response = new ListOwnersSoapResponse();
        response.setResult(ownersService.listOwners(request.getLastName()));
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateOwnerRequest")
    @ResponsePayload
    public CreateOwnerSoapResponse createOwner(@RequestPayload CreateOwnerSoapRequest request) {
        CreateOwnerSoapResponse response = new CreateOwnerSoapResponse();
        response.setResult(ownersService.createOwner(request.getBody()));
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetOwnerByIdRequest")
    @ResponsePayload
    public GetOwnerByIdSoapResponse getOwnerById(@RequestPayload GetOwnerByIdSoapRequest request) {
        GetOwnerByIdSoapResponse response = new GetOwnerByIdSoapResponse();
        response.setResult(ownersService.getOwnerById(request.getOwnerId()));
        response.setSuccess(true);
        return response;
    }

}
