package com.holdemhavenus.holdemhaven.requestDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VerifyBetRequest {
    private BigDecimal anteBetAmount;
    private BigDecimal tripsBetAmount;
}
