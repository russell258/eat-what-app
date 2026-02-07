package com.eatwhat.backend.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.eatwhat.backend.dto.ApiResponse;
import com.eatwhat.backend.dto.SessionCreateRequest;
import com.eatwhat.backend.model.Session;
import com.eatwhat.backend.service.SessionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/sessions")
@CrossOrigin(origins = "http://localhost:3000") // for react frontend
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);
    
    @Autowired
    private SessionService sessionSvc;

    @PostMapping
    public ResponseEntity<ApiResponse<Session>> createSession(@Valid @RequestBody SessionCreateRequest request) {
        logger.info("Creating session for user: {}", request.getUsername());
        try {
            Session session = sessionSvc.createSession(request.getUsername());
            logger.info("Session created successfully: {}", session.getSessionCode());
            return ResponseEntity.ok(ApiResponse.success(session, "Session created successfully"));
        } catch (Exception e) {
            logger.error("Error creating session for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating session: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/{sessionCode}")
    public ResponseEntity<ApiResponse<Session>> getSession(@PathVariable String sessionCode) {
        logger.info("Retrieving session: {}", sessionCode);
        try{
            Optional<Session> sessionOpt = sessionSvc.getSessionByCode(sessionCode);
            if(sessionOpt.isPresent()){
                logger.info("Session found: {}", sessionCode);
                return ResponseEntity.ok(ApiResponse.success(sessionOpt.get()));
            } else {
                logger.warn("Session not found: {}", sessionCode);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("Session not found"));
            }
        } catch (Exception e) {
            logger.error("Error retrieving session: {}", sessionCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving session: " + e.getMessage(), 500));
        }
    }

    @PutMapping("/{sessionCode}/lock")
    public ResponseEntity<ApiResponse<SessionStatusResponse>> lockSession(@PathVariable String sessionCode) {
        logger.info("Locking session: {}", sessionCode);
        try {
            boolean isLocked = sessionSvc.isSessionLocked(sessionCode);
            logger.info("Session lock status: sessionCode={}, isLocked={}", sessionCode, isLocked);
            
            SessionStatusResponse response = new SessionStatusResponse(sessionCode, isLocked);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            logger.error("Error locking session: {}", sessionCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error locking session: " + e.getMessage(), 500));
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