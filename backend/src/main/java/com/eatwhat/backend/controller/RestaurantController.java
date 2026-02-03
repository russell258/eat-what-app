package com.eatwhat.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eatwhat.backend.dto.RestaurantSubmitRequest;
import com.eatwhat.backend.model.Restaurant;
import com.eatwhat.backend.service.RestaurantService;

@RestController
@RequestMapping("/api/sessions/{sessionCode}/restaurants")
@CrossOrigin(origins = "https://localhost:3000") // for react frontend
public class RestaurantController {
    
    @Autowired
    private RestaurantService restaurantSvc;

    @PostMapping
    public ResponseEntity<?> addRestaurant
        (@PathVariable String sessionCode, @RequestBody RestaurantSubmitRequest request) {

            try{
                Restaurant restaurant = restaurantSvc.submitRestaurant(
                    sessionCode, request.getRestaurantName(), request.getSubmittedBy());
                return ResponseEntity.ok(restaurant);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error submitting restaurant: " + e.getMessage());
            }
        }
    
    @GetMapping
    public ResponseEntity<?> getRestaurants(@PathVariable String sessionCode) {
        try{
            List<Restaurant> restaurants = restaurantSvc.getRestaurantsBySession(sessionCode);
            return ResponseEntity.ok(restaurants);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving restaurants: " + e.getMessage());
        }
    }

    @GetMapping("/random")
    public ResponseEntity<?> getRandomRestaurant(@PathVariable String sessionCode) {
        try{
            Restaurant randomRestaurant = restaurantSvc.getRandomRestaurant(sessionCode);
            return ResponseEntity.ok(randomRestaurant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving random restaurant: " + e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getRestaurantCount(@PathVariable String sessionCode) {
        try {
            long count = restaurantSvc.getRestaurantCount(sessionCode);
            return ResponseEntity.ok(new RestaurantCountResponse(count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting restaurant count: " + e.getMessage());
        }
    }

    @GetMapping("/can-request-random/{username}")
    public ResponseEntity<?> canRequestRandom(
        @PathVariable String sessionCode, @PathVariable String username) {
            try{
                boolean canRequest = restaurantSvc.canRequestRandom(sessionCode, username);
                return ResponseEntity.ok(new CanRequestRandomResponse(canRequest));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking request permission: " + e.getMessage());
            }
    }

    public static class RestaurantCountResponse {
        private long count;

        public RestaurantCountResponse(long count) {
            this.count = count;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }

    public static class CanRequestRandomResponse {
        private boolean canRequest;

        public CanRequestRandomResponse(boolean canRequest) {
            this.canRequest = canRequest;
        }

        public boolean isCanRequest() {
            return canRequest;
        }

        public void setCanRequest(boolean canRequest) {
            this.canRequest = canRequest;
        }
    }

}