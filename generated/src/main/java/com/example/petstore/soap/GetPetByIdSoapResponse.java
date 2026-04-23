package com.example.petstore.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import com.example.petstore.model.Pet;

@XmlRootElement(namespace = "http://com.example.petstore/ws/pets")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPetByIdSoapResponse {

    private boolean success;
    private Pet result;
    public Pet getResult() { return result; }
    public void setResult(Pet result) { this.result = result; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
