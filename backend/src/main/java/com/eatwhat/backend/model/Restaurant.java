package com.eatwhat.backend.model;

import java.time.LocalDateTime;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
public class Restaurant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Restaurant name is required")
    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    @NotBlank(message = "Submitted by is required")
    @Column(nullable = false)
    private String submittedBy;

    @NotNull(message = "Sesssion is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private Session session;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    public Restaurant(String restaurantName, String submittedBy, Session session) {
        this.restaurantName = restaurantName;
        this.submittedBy = submittedBy;
        this.session = session;
        this.submittedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", restaurantName='" + restaurantName + '\'' +
                ", submittedBy='" + submittedBy + '\'' +
                ", session=" + (session != null ? session.getId() : null) +
                ", submittedAt=" + submittedAt +
                '}';
    }
    

}
