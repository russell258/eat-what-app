package com.eatwhat.backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.eatwhat.backend.dto.ApiResponse;
import com.eatwhat.backend.dto.RestaurantSubmitRequest;
import com.eatwhat.backend.model.Restaurant;
import com.eatwhat.backend.service.RestaurantService;

@RestController
@RequestMapping("/api/v1/sessions/{sessionCode}/restaurants")
@CrossOrigin(origins = "http://localhost:3000") // for react frontend
public class RestaurantController {
    
    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);
    
    @Autowired
    private RestaurantService restaurantSvc;

    @PostMapping
    public ResponseEntity<ApiResponse<Restaurant>> addRestaurant
        (@PathVariable String sessionCode, @RequestBody RestaurantSubmitRequest request) {

            logger.info("Submitting restaurant: sessionCode={}, restaurantName={}, submittedBy={}", 
                       sessionCode, request.getRestaurantName(), request.getSubmittedBy());
            try{
                Restaurant restaurant = restaurantSvc.submitRestaurant(
                    sessionCode, request.getRestaurantName(), request.getSubmittedBy());
                logger.info("Restaurant submitted successfully: {}", restaurant.getId());
                return ResponseEntity.ok(ApiResponse.success(restaurant, "Restaurant submitted successfully"));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid restaurant submission: sessionCode={}, error={}", sessionCode, e.getMessage());
                return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), 400));
            } catch (Exception e) {
                logger.error("Error submitting restaurant: sessionCode={}", sessionCode, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error submitting restaurant: " + e.getMessage(), 500));
            }
        }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Restaurant>>> getRestaurants(@PathVariable String sessionCode) {
        logger.info("Retrieving restaurants for session: {}", sessionCode);
        try{
            List<Restaurant> restaurants = restaurantSvc.getRestaurantsBySession(sessionCode);
            logger.info("Retrieved {} restaurants for session: {}", restaurants.size(), sessionCode);
            return ResponseEntity.ok(ApiResponse.success(restaurants));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid session for restaurant retrieval: sessionCode={}, error={}", sessionCode, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage(), 400));
        } catch (Exception e) {
            logger.error("Error retrieving restaurants for session: {}", sessionCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving restaurants: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/random")
    public ResponseEntity<ApiResponse<Restaurant>> getRandomRestaurant(@PathVariable String sessionCode) {
        logger.info("Getting random restaurant for session: {}", sessionCode);
        try{
            Restaurant randomRestaurant = restaurantSvc.getRandomRestaurant(sessionCode);
            logger.info("Random restaurant selected: sessionCode={}, restaurantId={}", 
                       sessionCode, randomRestaurant.getId());
            return ResponseEntity.ok(ApiResponse.success(randomRestaurant, "Random restaurant selected"));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid session for random restaurant: sessionCode={}, error={}", sessionCode, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage(), 400));
        } catch (Exception e) {
            logger.error("Error retrieving random restaurant for session: {}", sessionCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving random restaurant: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<RestaurantCountResponse>> getRestaurantCount(@PathVariable String sessionCode) {
        logger.info("Getting restaurant count for session: {}", sessionCode);
        try {
            long count = restaurantSvc.getRestaurantCount(sessionCode);
            logger.info("Restaurant count for session {}: {}", sessionCode, count);
            
            RestaurantCountResponse response = new RestaurantCountResponse(count);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid session for count retrieval: sessionCode={}, error={}", sessionCode, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage(), 400));
        } catch (Exception e) {
            logger.error("Error getting restaurant count for session: {}", sessionCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error getting restaurant count: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/can-request-random/{username}")
    public ResponseEntity<ApiResponse<CanRequestRandomResponse>> canRequestRandom(
        @PathVariable String sessionCode, @PathVariable String username) {
            logger.info("Checking random request permission: sessionCode={}, username={}", sessionCode, username);
            try{
                boolean canRequest = restaurantSvc.canRequestRandom(sessionCode, username);
                logger.info("Random request permission check: sessionCode={}, username={}, canRequest={}", 
                           sessionCode, username, canRequest);
                
                CanRequestRandomResponse response = new CanRequestRandomResponse(canRequest);
                return ResponseEntity.ok(ApiResponse.success(response));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid session for permission check: sessionCode={}, username={}, error={}", 
                           sessionCode, username, e.getMessage());
                return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage(), 400));
            } catch (Exception e) {
                logger.error("Error checking random request permission: sessionCode={}, username={}", 
                            sessionCode, username, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error checking request permission: " + e.getMessage(), 500));
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