package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VerifyBetResponse {
    private boolean isSuccess;
    private String message;
    private BigDecimal accountBalance;

    public VerifyBetResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
