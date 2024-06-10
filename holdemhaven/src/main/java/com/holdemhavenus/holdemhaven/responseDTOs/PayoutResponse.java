package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PayoutResponse {
    private boolean isSuccess;
    private String message;
    private BigDecimal antePayout;
    private BigDecimal dealerPayout;
    private BigDecimal tripsPayout;
    private BigDecimal playPayout;
    private BigDecimal totalPayout;
    private BigDecimal accountBalance;

    public PayoutResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public PayoutResponse(boolean isSuccess, String message, BigDecimal accountBalance, BigDecimal antePayout, BigDecimal dealerPayout, BigDecimal tripsPayout, BigDecimal playPayout, BigDecimal totalPayout) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.accountBalance = accountBalance;
        this.antePayout = antePayout;
        this.dealerPayout = dealerPayout;
        this.tripsPayout = tripsPayout;
        this.playPayout  = playPayout;
        this.totalPayout = totalPayout;
    }
}
