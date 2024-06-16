package com.holdemhavenus.holdemhaven.requestDTOs;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SaveHandRequest {
    private Long playerId;
    private BigDecimal anteBet;
    private BigDecimal playBet;
    private BigDecimal tripsBet;
    private String winner;
    private BigDecimal playerPayout;
}
