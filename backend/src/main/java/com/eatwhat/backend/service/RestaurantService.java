package com.eatwhat.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eatwhat.backend.model.Restaurant;
import com.eatwhat.backend.model.Session;
import com.eatwhat.backend.repository.RestaurantRepository;

@Service
public class RestaurantService {
    
    @Autowired
    private RestaurantRepository restaurantRepo;
    
    @Autowired
    private SessionService sessionSvc;

    private final Random random = new Random();

    public Restaurant submitRestaurant(String sessionCode, String restaurantName, String submittedBy) {
        Optional<Session> sessionOpt = sessionSvc.getSessionByCode(sessionCode);
        if (!sessionOpt.isPresent()) {
            throw new IllegalArgumentException("Session not found: " + sessionCode);
        }
        
        Session session = sessionOpt.get();

        if (session.isLocked()) {
            throw new IllegalStateException("Cannot submit restaurants to locked session: " + sessionCode);
        }

        Restaurant restaurant = new Restaurant(restaurantName, submittedBy, session);
        return restaurantRepo.save(restaurant);
    }

    public List<Restaurant> getRestaurantsBySession(String sessionCode) {
        Optional<Session> sessionOpt = sessionSvc.getSessionByCode(sessionCode);
        if (sessionOpt.isEmpty()) {
            throw new IllegalArgumentException("Session not found: " + sessionCode);
        }
        return restaurantRepo.findBySessionOrderBySubmittedAtAsc(sessionOpt.get());
    }

    public Restaurant getRandomRestaurant(String sessionCode) {
        Optional<Session> sessionOpt = sessionSvc.getSessionByCode(sessionCode);
        if (sessionOpt.isEmpty()) {
            throw new IllegalArgumentException("Session not found: " + sessionCode);
        }

        Session session = sessionOpt.get();
        List<Restaurant> restaurants = restaurantRepo.findBySession(session);
        
        if (restaurants.isEmpty()) {
            throw new IllegalStateException("No restaurants available in session: " + sessionCode);
        }

        // Locl session after getting random restaurant
        sessionSvc.lockSession(sessionCode);

        // return random restaurant
        int randomIndex = random.nextInt(restaurants.size());
        return restaurants.get(randomIndex);
    }

    public long getRestaurantCount(String sessionCode) {
        Optional<Session> sessionOpt = sessionSvc.getSessionByCode(sessionCode);
        if (sessionOpt.isEmpty()) {
            throw new IllegalArgumentException("Session not found: " + sessionCode);
        }

        return restaurantRepo.countBySession(sessionOpt.get());
    }

    public String getFirstSubmitter(String sessionCode) {
        List<Restaurant> restaurants = getRestaurantsBySession(sessionCode);
        if (restaurants.isEmpty()) {
            return null;
        }
        return restaurants.get(0).getSubmittedBy();
    }

    // Check in controller before getRandomRestaurant
    public boolean canRequestRandom(String sessionCode, String username) {
        String firstSubmitter = getFirstSubmitter(sessionCode);
        return firstSubmitter != null && firstSubmitter.equals(username);
    }

}