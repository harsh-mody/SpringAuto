package com.example.petstore.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://com.example.petstore/ws/owners")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListOwnersSoapRequest {

    private String lastName;

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
