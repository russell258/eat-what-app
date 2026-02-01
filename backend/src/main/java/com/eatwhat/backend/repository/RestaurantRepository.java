package com.eatwhat.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eatwhat.backend.model.Restaurant;
import com.eatwhat.backend.model.Session;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    
    List<Restaurant> findBySession(Session session);

    List<Restaurant> findBySessionOrderBySubmittedAtAsc(Session session);

    long countBySession(Session session);

}
