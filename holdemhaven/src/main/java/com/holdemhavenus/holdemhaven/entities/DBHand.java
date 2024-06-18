package com.holdemhavenus.holdemhaven.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
//Database hand entity
//Contains all info that needs to be persisted in order to accurately describe hand action
public class DBHand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long handId;
    private Long playerId;
    private BigDecimal anteBet;
    private BigDecimal dealerBet;
    private BigDecimal playBet;
    private BigDecimal tripsBet;
    private String boardCards;
    private String playerHoleCards;
    private String dealerHoleCards;
    private String result;
    private BigDecimal playerPayout;
}
