package com.generated.api.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/** Physical address */
public class Address implements Serializable {

    @Size(max = 200)
    private String street;
    @Size(max = 100)
    private String city;
    @Size(max = 50)
    private String state;
    @Pattern(regexp = "^[A-Z0-9 \\-]{3,10}$")
    @Size(max = 20)
    private String postalCode;
    @Pattern(regexp = "^[A-Z]{2}$")
    @Size(max = 2)
    private String country;

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
