package com.holdemhavenus.holdemhaven.requestDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FoldPayoutRequest {
    private BigDecimal tripsBetAmount;
    private int playerHandRanking;
}
