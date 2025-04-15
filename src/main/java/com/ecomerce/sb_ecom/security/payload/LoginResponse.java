package com.ecomerce.sb_ecom.security.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor

public class LoginResponse {
    private Long id;
    private String username;
    private List<String> roles;
    public LoginResponse( Long id,String username,List<String> roles) {
        this.id=id;
        this.roles = roles;
        this.username = username;
    }


}
