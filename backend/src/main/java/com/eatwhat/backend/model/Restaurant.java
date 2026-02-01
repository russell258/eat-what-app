package com.eatwhat.backend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.NotFound;

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
import lombok.Setter;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
public class Restaurant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Restaurant name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Submitted by is required")
    @Column(nullable = false)
    private String submittedBy;

    @NotNull(message = "Sesssion is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)  
    private Session session;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    public Restaurant(String name, String submittedBy, Session session) {
        this.name = name;
        this.submittedBy = submittedBy;
        this.session = session;
        this.submittedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", submittedBy='" + submittedBy + '\'' +
                ", session=" + (session != null ? session.getId() : null) +
                ", submittedAt=" + submittedAt +
                '}';
    }
    

}
