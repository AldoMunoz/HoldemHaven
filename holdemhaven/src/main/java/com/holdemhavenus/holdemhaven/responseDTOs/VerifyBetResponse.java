package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VerifyBetResponse extends BaseResponse {
    private boolean isSuccess;
    private String message;
    private BigDecimal accountBalance;

    public VerifyBetResponse(boolean isSuccess, String message) {
        super(isSuccess, message);
    }
}
