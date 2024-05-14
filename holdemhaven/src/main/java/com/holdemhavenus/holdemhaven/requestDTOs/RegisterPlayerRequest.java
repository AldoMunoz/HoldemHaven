package com.holdemhavenus.holdemhaven.requestDTOs;

import lombok.Data;

@Data
public class RegisterPlayerRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
}
