package com.holdemhavenus.holdemhaven.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
//Entity used to build the find and represent the best five card hand using a player's hole cards and the community cards
public class Hand {
    private Card[] holeCards;
    private ArrayList<Card> communityCards;
    private HandRanking handRanking;
    private Card[] fiveCardHand;

    public Hand(Card[] holeCards, ArrayList<Card> communityCards) {
        this.holeCards = holeCards;
        this.communityCards = communityCards;
        this.fiveCardHand = new Card[5];
    }
}
