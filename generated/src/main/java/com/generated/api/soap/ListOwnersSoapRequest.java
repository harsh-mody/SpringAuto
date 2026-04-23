package com.generated.api.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://com.generated.api/ws/owners")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListOwnersSoapRequest {

    private String lastName;

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
