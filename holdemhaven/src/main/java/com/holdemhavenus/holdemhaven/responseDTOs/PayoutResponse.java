package com.holdemhavenus.holdemhaven.responseDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PayoutResponse extends BaseResponse {
    private boolean isSuccess;
    private String message;
    private BigDecimal antePayout;
    private BigDecimal dealerPayout;
    private BigDecimal tripsPayout;
    private BigDecimal playPayout;
    private BigDecimal totalPayout;
    private BigDecimal accountBalance;

    public PayoutResponse(boolean isSuccess, String message) {
        super(isSuccess, message);
    }

    public PayoutResponse(boolean isSuccess, String message, BigDecimal accountBalance, BigDecimal antePayout, BigDecimal dealerPayout, BigDecimal tripsPayout, BigDecimal playPayout, BigDecimal totalPayout) {
        super(isSuccess, message);
        this.accountBalance = accountBalance;
        this.antePayout = antePayout;
        this.dealerPayout = dealerPayout;
        this.tripsPayout = tripsPayout;
        this.playPayout  = playPayout;
        this.totalPayout = totalPayout;
    }
}
