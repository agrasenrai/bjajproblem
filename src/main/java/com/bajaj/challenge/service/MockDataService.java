package com.bajaj.challenge.service;

import com.bajaj.challenge.model.InitialResponse;
import com.bajaj.challenge.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This service provides mock data for testing when the real API is not available.
 */
@Service
public class MockDataService {

    /**
     * Creates a mock response that resembles what would be expected from the actual API.
     * This is used for testing the application when the real API is not responding as expected.
     *
     * @return A mock InitialResponse object with test data
     */
    public InitialResponse createMockResponse() {
        InitialResponse mockResponse = new InitialResponse();
        
        // Mock the API response fields
        mockResponse.setWebhookUrl("https://mockwebhook.bajajfinserv.in/webhook");
        mockResponse.setAccessToken("mock-jwt-token-12345");
        
        // Create mock user network for the Nth-Level Followers problem
        List<User> users = createMockUserNetwork();
        
        // Set up the data map with problem details
        Map<String, Object> data = new HashMap<>();
        data.put("findId", 1);  // Start from user ID 1
        data.put("level", 2);   // Find users at level 2
        data.put("users", convertUsersToMap(users));
        
        mockResponse.setData(data);
        
        return mockResponse;
    }
    
    /**
     * Creates a mock network of users with follow relationships.
     * Example: If user 1 follows users 2 and 3, and user 2 follows user 4, 
     * and user 3 follows users 4 and 5, then:
     * - Level 1 followers of user 1 are [2,3]
     * - Level 2 followers of user 1 are [4,5]
     *
     * @return List of User objects forming a network
     */
    private List<User> createMockUserNetwork() {
        List<User> users = new ArrayList<>();
        
        // User 1 follows users 2 and 3
        User user1 = new User(1, Arrays.asList(2, 3));
        
        // User 2 follows user 4
        User user2 = new User(2, Arrays.asList(4));
        
        // User 3 follows users 4 and 5
        User user3 = new User(3, Arrays.asList(4, 5));
        
        // User 4 follows no one
        User user4 = new User(4, new ArrayList<>());
        
        // User 5 follows no one
        User user5 = new User(5, new ArrayList<>());
        
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        
        return users;
    }
    
    /**
     * Converts a list of User objects to a list of maps for serialization.
     * This mimics how the data would be structured in the API response.
     *
     * @param users The list of User objects
     * @return A list of maps representing the users
     */
    private List<Map<String, Object>> convertUsersToMap(List<User> users) {
        List<Map<String, Object>> userMaps = new ArrayList<>();
        
        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("follows", user.getFollows());
            userMaps.add(userMap);
        }
        
        return userMaps;
    }
} 