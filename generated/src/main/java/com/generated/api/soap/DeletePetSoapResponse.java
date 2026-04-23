package com.generated.api.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://com.generated.api/ws/pets")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeletePetSoapResponse {

    private boolean success;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
