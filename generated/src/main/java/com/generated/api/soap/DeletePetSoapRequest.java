package com.generated.api.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://com.generated.api/ws/pets")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeletePetSoapRequest {

    private Long petId;

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
}
