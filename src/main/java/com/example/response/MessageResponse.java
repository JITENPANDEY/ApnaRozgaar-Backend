package com.example.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Integer status=200;
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
