package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordResponse extends BaseResponse {
    public ChangePasswordResponse(boolean isSuccess, String message) {
        super(isSuccess, message);
    }
}
