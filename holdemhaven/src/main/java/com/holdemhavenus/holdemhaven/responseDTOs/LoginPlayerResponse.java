package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoginPlayerResponse {
    private boolean isSuccess;
    private String message;
    private String playerUsername;
    private BigDecimal accountBalance;
}
