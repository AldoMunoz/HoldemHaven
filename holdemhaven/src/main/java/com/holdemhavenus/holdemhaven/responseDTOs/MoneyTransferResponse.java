package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MoneyTransferResponse {
    private boolean isSuccess;
    private String message;
    private BigDecimal amount;

    public MoneyTransferResponse(boolean isSuccess, String message, BigDecimal amount) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.amount = amount;
    }
    public MoneyTransferResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public MoneyTransferResponse() {}


}
