package com.generated.api.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import com.generated.api.model.Owner;

@XmlRootElement(namespace = "http://com.generated.api/ws/owners")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateOwnerSoapResponse {

    private boolean success;
    private Owner result;
    public Owner getResult() { return result; }
    public void setResult(Owner result) { this.result = result; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
