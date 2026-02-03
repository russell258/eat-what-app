package com.eatwhat.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eatwhat.backend.model.User;
import com.eatwhat.backend.repository.UserRepository;
import com.eatwhat.backend.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://localhost:3000") // for react frontend
public class UserController {
    
    @Autowired
    private UserService userSvc;

    @Autowired
    private UserRepository userRepo;

    @GetMapping("")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @GetMapping("/validate/{username}")
    public ResponseEntity<?> validateUser(@PathVariable String username) {
        try {
            boolean userExists = userSvc.userExists(username);
            boolean canInitiate = userSvc.canInitiateSession(username);
            return ResponseEntity.ok(new UserValidationResponse(username, userExists, canInitiate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error validating user: " + e.getMessage());
        }
    }

    @GetMapping("/exists/{username}")
    public ResponseEntity<?> checkUserExists(@PathVariable String username) {
        try {
            boolean exists = userSvc.userExists(username);
            return ResponseEntity.ok(new UserExistsResponse(username, exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error checking user existence: " + e.getMessage());
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