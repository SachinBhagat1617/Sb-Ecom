package com.ecomerce.sb_ecom.security.payload;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponse {
    @Getter
    @Setter
    private String message;
}
