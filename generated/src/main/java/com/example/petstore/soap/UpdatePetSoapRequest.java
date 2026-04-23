package com.example.petstore.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import com.example.petstore.model.UpdatePetRequest;

@XmlRootElement(namespace = "http://com.example.petstore/ws/pets")
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdatePetSoapRequest {

    private Long petId;
    private UpdatePetRequest body;

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
    public UpdatePetRequest getBody() { return body; }
    public void setBody(UpdatePetRequest body) { this.body = body; }
}
