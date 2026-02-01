package com.eatwhat.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sessions")
@Getter
@Setter
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Session code is required")
    @Column(name = "session_code", nullable = false, unique = true)
    private String sessionCode;

    @NotNull(message = "Initiator is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "locked _at")
    private LocalDateTime lockedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Restaurant> restaurants = new ArrayList<>();

    public enum SessionStatus {
        ACTIVE, LOCKED
    }

    public Session(String sessionCode, User initiator) {
        this.sessionCode = sessionCode;
        this.initiator = initiator; 
        this.status = SessionStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    // lock session
    public void lockSession() {
        this.status = SessionStatus.LOCKED;
        this.lockedAt = LocalDateTime.now();
    }

    // check if locked
    public boolean isLocked() {
        return this.status == SessionStatus.LOCKED;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", sessionCode='" + sessionCode + '\'' +
                ", initiator=" + (initiator != null ? initiator.getUsername() : null) +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", lockedAt=" + lockedAt +
                '}';
    }

}
