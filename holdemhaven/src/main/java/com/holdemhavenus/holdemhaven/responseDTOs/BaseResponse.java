package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse {
    private boolean isSuccess;
    private String message;

    public BaseResponse () {}

    public BaseResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
