package com.holdemhavenus.holdemhaven.requestDTOs;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginPlayerRequest {
    private String username;
    private String password;
}
