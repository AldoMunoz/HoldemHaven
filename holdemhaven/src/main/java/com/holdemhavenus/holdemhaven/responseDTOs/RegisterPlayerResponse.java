package com.holdemhavenus.holdemhaven.responseDTOs;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterPlayerResponse extends BaseResponse {
    private boolean isSuccess;
    private String message;

    public RegisterPlayerResponse(boolean isSuccess, String message) {
        super(isSuccess, message);
    }
}
