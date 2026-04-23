package com.generated.api.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://com.generated.api/ws/owners")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListOwnersSoapResponse {

    private boolean success;
    private Object result;
    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
