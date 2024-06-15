package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeUsernameResponse extends BaseResponse {
    private String username;

    public ChangeUsernameResponse (boolean isSuccess, String message) {
        super(isSuccess, message);

    }
    public ChangeUsernameResponse (boolean isSuccess, String message, String username) {
        super(isSuccess, message);
        this.username = username;
    }
}
