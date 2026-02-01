package com.eatwhat.backend.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eatwhat.backend.dto.SessionCreateRequest;
import com.eatwhat.backend.model.Session;
import com.eatwhat.backend.service.SessionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
public class SessionController {

    @Autowired
    private SessionService sessionSvc;

    @PostMapping
    public ResponseEntity<?> createSession(@Valid @RequestBody SessionCreateRequest request) {
        try {
            Session session = sessionSvc.createSession(request.getUsername());
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating session: " + e.getMessage());
        }
    }

    @GetMapping("/{sessionCode}")
    public ResponseEntity<?> getSession(@PathVariable String sessionCode) {
        try{
            Optional<Session> sessionOpt = sessionSvc.getSessionByCode(sessionCode);
            if(sessionOpt.isPresent()){
                return ResponseEntity.ok(sessionOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving session: " + e.getMessage());
        }
    }

    @PutMapping("/{sessionCode}/lock")
    public ResponseEntity<?> lockSession(@PathVariable String sessionCode) {
        try {
            boolean isLocked = sessionSvc.isSessionLocked(sessionCode);
            return ResponseEntity.ok(new SessionStatusResponse(sessionCode, isLocked));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error locking session: " + e.getMessage());
        }
    }

    public static class SessionStatusResponse {
        private String sessionCode;
        private boolean locked;

        public SessionStatusResponse(String sessionCode, boolean locked) {
            this.sessionCode = sessionCode;
            this.locked = locked;
        }

        public String getSessionCode() {
            return this.sessionCode;
        }

        public void setSessionCode(String sessionCode) {
            this.sessionCode = sessionCode;
        }

        public boolean isLocked() {
            return this.locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }
    }

}