package com.generated.api.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import com.generated.api.model.UpdatePetRequest;

@XmlRootElement(namespace = "http://com.generated.api/ws/pets")
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdatePetSoapRequest {

    private Long petId;
    private UpdatePetRequest body;

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
    public UpdatePetRequest getBody() { return body; }
    public void setBody(UpdatePetRequest body) { this.body = body; }
}
