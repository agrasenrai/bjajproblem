package com.bajaj.challenge.service;

import com.bajaj.challenge.model.InitialRequest;
import com.bajaj.challenge.model.InitialResponse;
import com.bajaj.challenge.model.WebhookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiService {
    
    private static final String INITIAL_API_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";
    
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;
    
    public InitialResponse sendInitialRequest(InitialRequest request) {
        try {
            log.info("Sending initial request to: {}", INITIAL_API_URL);
            log.debug("Request body: {}", request);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<InitialRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<InitialResponse> responseEntity = restTemplate.postForEntity(
                INITIAL_API_URL, entity, InitialResponse.class);
            
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                log.error("API returned error status: {}", responseEntity.getStatusCode());
                throw new RestClientException("API returned error status: " + responseEntity.getStatusCode());
            }
            
            InitialResponse response = responseEntity.getBody();
            if (response == null) {
                log.error("API returned null response body");
                throw new RestClientException("API returned null response body");
            }
            
            log.info("Received response with webhook URL: {}", response.getWebhookUrl());
            return response;
        } catch (Exception e) {
            log.error("Error sending initial request: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Retryable(maxAttempts = 4)
    public void sendWebhookRequest(String webhookUrl, String accessToken, WebhookRequest request) {
        log.info("Sending webhook request to: {}", webhookUrl);
        log.debug("Webhook request body: {}", request);
        
        try {
            retryTemplate.execute(retryContext -> {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set("Authorization", "Bearer " + accessToken);
                    
                    HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                        webhookUrl, entity, String.class);
                    
                    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                        log.error("Webhook API returned error status: {}", responseEntity.getStatusCode());
                        throw new RestClientException("Webhook API returned error status: " + responseEntity.getStatusCode());
                    }
                    
                    String response = responseEntity.getBody();
                    log.info("Webhook response: {}", response);
                    return response;
                } catch (Exception e) {
                    log.error("Attempt {} failed: {}", retryContext.getRetryCount(), e.getMessage());
                    throw e;
                }
            });
        } catch (Exception e) {
            log.error("All webhook request attempts failed: {}", e.getMessage(), e);
            throw e;
        }
    }
} 