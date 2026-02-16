package com.minierp.mini_erp.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String role;
}