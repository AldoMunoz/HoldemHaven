package com.holdemhavenus.holdemhaven.responseDTOs;

import com.holdemhavenus.holdemhaven.entities.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class DealHandResponse {
    private Card[] playerHoleCards;

    public DealHandResponse(Card[] playerHoleCards) {
        this.playerHoleCards = playerHoleCards;
    }
}
