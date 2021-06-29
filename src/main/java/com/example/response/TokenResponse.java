package com.example.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private  Integer status=200;
    private String Role;
    private String Token;

    public TokenResponse(String token) {
        Token = token;
    }
    
    public TokenResponse(String token,String role) {
        Token = token;
        Role = role;
    }
}
