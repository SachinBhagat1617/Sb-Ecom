package com.ecomerce.sb_ecom.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames="email")
        })
// combination of name and email must be unique
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    @Size(max=20)
    @NotBlank
    @Column(name = "username")
    private String username;

    @Size(max=50)
    @NotBlank
    @Email
    @Column(name = "email")
    private String email;

    @Size
    @NotBlank
    @Column(name = "password")
    private String password;

    public User( String userName, String email, String password) {
        this.username=userName;
        this.email=email;
        this.password=password;
    }

    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
                fetch = FetchType.EAGER)
    @JoinTable(
            name="user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles=new HashSet<>();

    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name="user_address",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="address_id")
    )
    private List<Address> addresses=new ArrayList<>();// in many to many generally the side from data will enter like
                                                      // address will go from user table so this will contain joinTable
    @ToString.Exclude
    @OneToMany(mappedBy = "user",
                cascade = {CascadeType.PERSIST,CascadeType.MERGE},
                orphanRemoval = true)
    private Set<Product> products;
    /*private Set<Product> products;
This declares the association but doesn’t initialize the Set. So, if you try to call products.add(...) before assigning anything to products, you'll get a NullPointerException.

This approach relies on Hibernate/JPA to populate the collection when loading from the database.

Use this when: You’re confident that JPA will always initialize this collection before you access it.*/

    @ToString.Exclude
    @OneToOne(mappedBy ="user",cascade = {CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval = true)
    private Cart cart;



}
