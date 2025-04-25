package com.bajaj.challenge.model;

import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class InitialResponse {
    private String webhookUrl;
    private String accessToken;
    private Map<String, Object> data;
    
    // For Question 2 (Nth-Level Followers)
    private Integer findId;
    private Integer level;
    private List<User> users;
    
    public Integer getFindId() {
        if (findId != null) {
            return findId;
        }
        
        if (data != null && data.containsKey("findId")) {
            return Integer.valueOf(data.get("findId").toString());
        }
        
        return null;
    }
    
    public Integer getLevel() {
        if (level != null) {
            return level;
        }
        
        if (data != null && data.containsKey("level")) {
            return Integer.valueOf(data.get("level").toString());
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<User> getUsers() {
        if (users != null) {
            return users;
        }
        
        if (data != null && data.containsKey("users")) {
            List<Map<String, Object>> usersData = (List<Map<String, Object>>) data.get("users");
            return usersData.stream()
                    .map(userData -> {
                        User user = new User();
                        user.setId(Integer.valueOf(userData.get("id").toString()));
                        if (userData.containsKey("follows")) {
                            List<Integer> follows = (List<Integer>) userData.get("follows");
                            user.setFollows(follows);
                        }
                        return user;
                    })
                    .collect(Collectors.toList());
        }
        
        return null;
    }
} 