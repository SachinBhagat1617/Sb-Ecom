package com.ecomerce.sb_ecom.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="role_id")
    private Long roleId;

    @ToString.Exclude
    @Column(length=20, name="role_name")
    @Enumerated(EnumType.STRING) // because by default enumType is integer so we convert it to string
    private AppRole roleName;

    public Role(AppRole roleName) {
        this.roleName=roleName;
    }



}
