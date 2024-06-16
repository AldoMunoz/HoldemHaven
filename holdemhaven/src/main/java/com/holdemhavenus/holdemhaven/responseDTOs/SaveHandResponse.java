package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveHandResponse extends BaseResponse {
    public SaveHandResponse(boolean isSuccess, String message) {
        super(isSuccess, message);
    }
}
