package com.example.petstore.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import com.example.petstore.model.CreateOwnerRequest;

@XmlRootElement(namespace = "http://com.example.petstore/ws/owners")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateOwnerSoapRequest {

    private CreateOwnerRequest body;

    public CreateOwnerRequest getBody() { return body; }
    public void setBody(CreateOwnerRequest body) { this.body = body; }
}
