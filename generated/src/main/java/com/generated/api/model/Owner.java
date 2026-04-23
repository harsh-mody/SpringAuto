package com.generated.api.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/** A pet owner */
public class Owner implements Serializable {

    @NotNull
    private Long id;
    @NotBlank
    @Size(min = 1, max = 50)
    private String firstName;
    @NotBlank
    @Size(min = 1, max = 50)
    private String lastName;
    @NotBlank
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")
    @Size(max = 255)
    private String email;
    @Pattern(regexp = "^\\+?[0-9\\s\\-().]{7,20}$")
    @Size(max = 20)
    private String phone;
    @Valid
    private Address address;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}
