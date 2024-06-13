package com.holdemhavenus.holdemhaven.responseDTOs;

import com.holdemhavenus.holdemhaven.entities.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDealerHandResponse {
    private Card[] dealerHoleCards;
    private String dealerHandToString;

    public GetDealerHandResponse(Card[] dealerHoleCards, String dealerHandToString) {
        this.dealerHoleCards = dealerHoleCards;
        this.dealerHandToString = dealerHandToString;
    }
}
