package com.holdemhavenus.holdemhaven.responseDTOs;

import com.holdemhavenus.holdemhaven.entities.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class DealHandResponse {
    private ArrayList<Card> playerHoleCards;

    public DealHandResponse(ArrayList<Card> playerHoleCards) {
        this.playerHoleCards = playerHoleCards;
    }
}
