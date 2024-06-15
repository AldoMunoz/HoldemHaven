package com.holdemhavenus.holdemhaven.responseDTOs;

import com.holdemhavenus.holdemhaven.requestDTOs.LoginPlayerRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoginPlayerResponse extends BaseResponse {
    private String playerUsername;
    private BigDecimal accountBalance;

    public LoginPlayerResponse(boolean isSuccess, String message, String playerUsername, BigDecimal accountBalance) {
        super(isSuccess, message);
        this.playerUsername = playerUsername;
        this.accountBalance = accountBalance;
    }

    public LoginPlayerResponse(boolean isSuccess, String message) {
        super(isSuccess, message);
    }
}
