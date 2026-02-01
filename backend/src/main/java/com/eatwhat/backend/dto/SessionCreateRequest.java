package com.eatwhat.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionCreateRequest {
    
    @NotBlank(message = "Username is required")
    private String username;

    public SessionCreateRequest(String username) {
        this.username = username;
    }
}
