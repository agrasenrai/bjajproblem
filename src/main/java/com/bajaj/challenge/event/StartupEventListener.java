package com.bajaj.challenge.event;

import com.bajaj.challenge.model.InitialRequest;
import com.bajaj.challenge.model.InitialResponse;
import com.bajaj.challenge.model.User;
import com.bajaj.challenge.model.WebhookRequest;
import com.bajaj.challenge.service.ApiService;
import com.bajaj.challenge.service.MockDataService;
import com.bajaj.challenge.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartupEventListener {
    
    private final ApiService apiService;
    private final UserService userService;
    private final MockDataService mockDataService;
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application started, initiating the challenge workflow");
        
        try {
            // Step 1: Create the initial request
            InitialRequest initialRequest = new InitialRequest(
                "Agrasen Rai",
                "RA2211003010604",
                "ar8546@srmist.edu.in"
            );
            
            // Step 2: Send the initial request and get the response
            InitialResponse response;
            try {
                response = apiService.sendInitialRequest(initialRequest);
                log.info("Received response from API: {}", response);
                
                // Check if the response contains the required data
                if (response.getWebhookUrl() == null || 
                    response.getAccessToken() == null || 
                    response.getFindId() == null || 
                    response.getLevel() == null || 
                    response.getUsers() == null || 
                    response.getUsers().isEmpty()) {
                    
                    log.warn("API response is missing required data. Using mock data instead.");
                    response = mockDataService.createMockResponse();
                    log.info("Using mock data: {}", response);
                }
            } catch (Exception e) {
                log.warn("Error calling the API: {}. Using mock data instead.", e.getMessage());
                response = mockDataService.createMockResponse();
                log.info("Using mock data: {}", response);
            }
            
            // Step 3: Process the data to solve the problem (Nth-Level Followers)
            Integer findId = response.getFindId();
            Integer level = response.getLevel();
            List<User> users = response.getUsers();
            
            log.info("Processing problem with findId={}, level={}, users={}", findId, level, users.size());
            
            List<Integer> nthLevelFollowers = userService.findNthLevelFollowers(
                users, 
                findId,
                level
            );
            
            log.info("Found followers at level {}: {}", level, nthLevelFollowers);
            
            // Step 4: Create the webhook request with the results
            WebhookRequest webhookRequest = new WebhookRequest(
                initialRequest.getRegNo(),
                nthLevelFollowers
            );
            
            // Step 5: Send the webhook request with the solution
            try {
                apiService.sendWebhookRequest(
                    response.getWebhookUrl(),
                    response.getAccessToken(),
                    webhookRequest
                );
                log.info("Successfully submitted solution to webhook");
            } catch (Exception e) {
                log.error("Failed to submit solution to webhook: {}", e.getMessage());
                log.info("Would have submitted this solution: {}", webhookRequest);
            }
            
            log.info("Challenge workflow completed");
            
        } catch (Exception e) {
            log.error("Error during challenge workflow execution: {}", e.getMessage(), e);
        }
    }
} 