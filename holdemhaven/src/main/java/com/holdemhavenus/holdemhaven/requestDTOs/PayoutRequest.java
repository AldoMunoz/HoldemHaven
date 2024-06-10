package com.holdemhavenus.holdemhaven.requestDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PayoutRequest {
    private BigDecimal anteBetAmount;
    private BigDecimal tripsBetAmount;
    private BigDecimal playBetAmount;
    private char winner;
    private int playerHandRanking;
    private int dealerHandRanking;
}
