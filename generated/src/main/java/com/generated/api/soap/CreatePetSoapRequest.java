package com.generated.api.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import com.generated.api.model.CreatePetRequest;

@XmlRootElement(namespace = "http://com.generated.api/ws/pets")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreatePetSoapRequest {

    private CreatePetRequest body;

    public CreatePetRequest getBody() { return body; }
    public void setBody(CreatePetRequest body) { this.body = body; }
}
