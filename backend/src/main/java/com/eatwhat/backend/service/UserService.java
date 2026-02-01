package com.eatwhat.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eatwhat.backend.model.User;
import com.eatwhat.backend.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public User saveUser(User user) {
        return userRepo.save(user);
    }

    public boolean canInitiateSession(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        return user.isPresent() && user.get().getRole() == User.UserRole.SESSION_INITIATOR;
    }

    public boolean userExists(String username) {
        return userRepo.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }

    public User createUser(String username, String email, User.UserRole role) {
        if (userExists(username)){
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (emailExists(email)){
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = new User(username, email, role);
        return userRepo.save(user);
    }

}