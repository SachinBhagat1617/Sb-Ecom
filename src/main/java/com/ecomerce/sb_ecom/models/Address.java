package com.ecomerce.sb_ecom.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="address_id")
    private Long addressId;
    
    @NotBlank
    @Size(min=5,message="Street name must be atleast 5 chars")
    private String street;

    @NotBlank
    @Size(min=5,message="Building name must be atleast 5 chars")
    private String buildingName;

    @NotBlank
    @Size(min=4,message="city name must be atleast 4 chars")
    private String city;

    @NotBlank
    @Size(min=2,message="Building name must be atleast 2 chars")
    private String state;

    @NotBlank
    @Size(min=2,message="Country name must be atleast 2 chars")
    private String country;

    @NotBlank
    @Size(min=6,message="Pincode name must be atleast 5 chars")
    private String pincode;

    @ToString.Exclude // when I call the Address Table by converting DTO toString then we want to exclude lists of user
    @ManyToMany(mappedBy = "addresses") // non owing side
    private List<User> users=new ArrayList<>();
    /*💥 What happens without @ToString.Exclude?
        Imagine this:
        Address has a list of User
        Each User has a list of Address
        Each of those Address has a list of User
        … and so on 🔁*/

    public Address(String street, String buildingName, String city, String state, String country, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }
}
