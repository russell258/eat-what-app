package com.eatwhat.backend.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eatwhat.backend.model.Session;
import com.eatwhat.backend.model.User;
import com.eatwhat.backend.repository.SessionRepository;

@Service
public class SessionService {
    
    @Autowired
    private SessionRepository sessionRepo;

    @Autowired
    private UserService userSvc;

    public Session createSession(String username) {
        Optional<User> user = userSvc.getUserByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + username);
        }

        if (!userSvc.canInitiateSession(username)) {
            throw new IllegalArgumentException("User is not authorized to intiate sessions: " + username);
        }

        String sessionCode = generateUniqueSessionCode();
        Session session = new Session(sessionCode, user.get());
        return sessionRepo.save(session);
    }

    public Optional<Session> getSessionByCode(String sessionCode) {
        return sessionRepo.findBySessionCode(sessionCode);
    }

    public Session lockSession(String sessionCode) {
        Optional<Session> sessionOpt = sessionRepo.findBySessionCode(sessionCode);
        if (sessionOpt.isEmpty()) {
            throw new IllegalArgumentException("Session not found: " + sessionCode);
        }

        Session session = sessionOpt.get();

        if (session.isLocked()) {
            throw new IllegalStateException("Session is already locked: " + sessionCode);
        }

        session.lockSession();
        return sessionRepo.save(session);
    }

    public boolean sessionExists(String sessionCode) {
        return sessionRepo.existsBySessionCode(sessionCode);
    }

    public boolean isSessionLocked(String sessionCode) {
        Optional<Session> session = sessionRepo.findBySessionCode(sessionCode);
        return session.isPresent() && session.get().isLocked();
    }

    private String generateUniqueSessionCode() {
        String sessionCode;
        do {
            sessionCode = generateSessionCode();
        } while (sessionRepo.existsBySessionCode(sessionCode));;
        return sessionCode;
    }

    private String generateSessionCode() {
        // generate 6 character uuid
        return UUID.randomUUID().toString().replace("-","").substring(0,6).toUpperCase();
    }
    

}