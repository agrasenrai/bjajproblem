package com.bajaj.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitialRequest {
    private String name;
    private String regNo;
    private String email;
} 