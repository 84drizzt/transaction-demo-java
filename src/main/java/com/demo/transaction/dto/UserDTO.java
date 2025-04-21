package com.demo.transaction.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
} 