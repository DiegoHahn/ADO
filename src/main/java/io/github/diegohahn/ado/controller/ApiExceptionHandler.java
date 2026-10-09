package io.github.diegohahn.ado.controller;

import java.net.http.HttpTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.diegohahn.ado.exceptions.AzureDevOpsApiException;
import io.github.diegohahn.ado.exceptions.InvalidTokenException;
import io.github.diegohahn.ado.exceptions.UserNotFoundException;

/**
 * Maps the domain exceptions that escape a controller to HTTP statuses, so an
 * Azure DevOps outage is not reported as "user not found" or as a generic 500.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(HttpTimeoutException.class)
    public ResponseEntity<String> handleTimeout(HttpTimeoutException e) {
        logger.warn("Azure DevOps request timed out: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Azure DevOps did not respond in time.");
    }

    @ExceptionHandler(AzureDevOpsApiException.class)
    public ResponseEntity<String> handleAzureDevOpsError(AzureDevOpsApiException e) {
        logger.error("Azure DevOps API error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Azure DevOps request failed.");
    }
}
