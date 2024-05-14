package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterPlayerResponse {
    private boolean isSuccess;
    private String message;
}
