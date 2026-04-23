package com.generated.api.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import com.generated.api.model.CreateOwnerRequest;

@XmlRootElement(namespace = "http://com.generated.api/ws/owners")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateOwnerSoapRequest {

    private CreateOwnerRequest body;

    public CreateOwnerRequest getBody() { return body; }
    public void setBody(CreateOwnerRequest body) { this.body = body; }
}
