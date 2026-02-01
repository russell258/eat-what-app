package com.eatwhat.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantSubmitRequest {
    
    @NotBlank(message = "Restaurant name is required")
    private String restaurantName;

    @NotBlank(message = "Submitted by is required")
    private String submittedBy;

    public RestaurantSubmitRequest(String restaurantName, String submittedBy) {
        this.restaurantName = restaurantName;
        this.submittedBy = submittedBy;
    }

}
