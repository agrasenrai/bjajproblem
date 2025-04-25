package com.bajaj.challenge.service;

import com.bajaj.challenge.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    /**
     * Find all users who are exactly n levels away from the starting user in the "follows" network.
     * Uses BFS algorithm to traverse the network and determine the level of each user.
     *
     * @param users List of users in the network
     * @param startId The starting user ID
     * @param level The level to find
     * @return List of user IDs at exactly the specified level
     */
    public List<Integer> findNthLevelFollowers(List<User> users, int startId, int level) {
        log.info("Finding level {} followers for user ID: {}", level, startId);
        
        // Create a map of users for quick access
        Map<Integer, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        
        // If the starting user doesn't exist, return empty list
        if (!userMap.containsKey(startId)) {
            log.warn("Starting user ID {} not found in the network", startId);
            return Collections.emptyList();
        }
        
        // BFS implementation
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> levels = new HashMap<>();
        
        // Start BFS from the starting user
        queue.add(startId);
        visited.add(startId);
        levels.put(startId, 0);
        
        while (!queue.isEmpty()) {
            Integer currentId = queue.poll();
            Integer currentLevel = levels.get(currentId);
            
            // If we're beyond the level we're looking for, no need to go further
            if (currentLevel > level) {
                break;
            }
            
            User currentUser = userMap.get(currentId);
            if (currentUser != null && currentUser.getFollows() != null) {
                for (Integer followId : currentUser.getFollows()) {
                    if (!visited.contains(followId)) {
                        queue.add(followId);
                        visited.add(followId);
                        levels.put(followId, currentLevel + 1);
                    }
                }
            }
        }
        
        // Filter users who are exactly at the nth level
        List<Integer> result = levels.entrySet().stream()
                .filter(entry -> entry.getValue() == level)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
        
        log.info("Found {} users at level {}: {}", result.size(), level, result);
        return result;
    }
} 