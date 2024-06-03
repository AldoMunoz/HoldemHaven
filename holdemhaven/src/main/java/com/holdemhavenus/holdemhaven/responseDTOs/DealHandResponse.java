package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DealHandResponse {
    private boolean isSuccess;
    private String message;
    private BigDecimal accountBalance;

    public DealHandResponse (boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
