package com.holdemhavenus.holdemhaven.responseDTOs;


import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteAccountResponse extends BaseResponse {
    public DeleteAccountResponse(boolean isSuccess, String message) {
        super(isSuccess, message);
    }
}
