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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eatwhat.backend.dto.ApiResponse;
import com.eatwhat.backend.model.User;
import com.eatwhat.backend.repository.UserRepository;
import com.eatwhat.backend.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:3000") // for react frontend
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userSvc;

    @Autowired
    private UserRepository userRepo;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        logger.info("Fetching all users");
        try {
            List<User> users = userRepo.findAll();
            logger.info("Successfully fetched {} users", users.size());
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            logger.error("Error fetching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch users", 500));
        }
    }

    @GetMapping("/validate/{username}")
    public ResponseEntity<ApiResponse<UserValidationResponse>> validateUser(@PathVariable String username) {
        logger.info("Validating user: {}", username);
        try {
            boolean userExists = userSvc.userExists(username);
            boolean canInitiate = userSvc.canInitiateSession(username);
            logger.info("User validation result: username={}, exists={}, canInitiate={}", 
                       username, userExists, canInitiate);
            
            UserValidationResponse response = new UserValidationResponse(username, userExists, canInitiate);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            logger.error("Error validating user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error validating user: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/exists/{username}")
    public ResponseEntity<ApiResponse<UserExistsResponse>> checkUserExists(@PathVariable String username) {
        logger.info("Checking user existence: {}", username);
        try {
            boolean exists = userSvc.userExists(username);
            logger.info("User existence check result: username={}, exists={}", username, exists);
            
            UserExistsResponse response = new UserExistsResponse(username, exists);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            logger.error("Error checking user existence: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error checking user existence: " + e.getMessage(), 500));
        }
    }

    public static class UserValidationResponse{
        private String username;
        private boolean exists;
        private boolean canInitiateSession;

        public UserValidationResponse(String username, boolean exists, boolean canInitiateSession) {
            this.username = username;
            this.exists = exists;
            this.canInitiateSession = canInitiateSession;
        }
        public String getUsername() {
            return this.username;
        }
        public boolean isExists() {
            return this.exists;
        }
        public boolean isCanInitiateSession() {
            return this.canInitiateSession;
        }
        public boolean setCanInitiateSession(boolean canInitiateSession) {
            return this.canInitiateSession = canInitiateSession;
        }
    }

    public static class UserExistsResponse {
        private String username;
        private boolean exists;

        public UserExistsResponse(String username, boolean exists) {
            this.username = username;
            this.exists = exists;
        }

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public boolean isExists(){
            return this.exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }
    }

}