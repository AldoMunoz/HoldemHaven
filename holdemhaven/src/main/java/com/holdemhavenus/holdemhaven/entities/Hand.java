package com.holdemhavenus.holdemhaven.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Hand {
    private Card[] playerCards;
    private ArrayList<Card> communityCards;
    //enum with the hand strength, x High to Royal
    private HandRanking handRanking;
    //5 cards that make up the players hand
    private Card[] fiveCardHand;

    //a board and a hand are passed when creating a new hand
    public Hand(Card[] playerCards, ArrayList<Card> communityCards) {
        this.playerCards = playerCards;
        this.communityCards = communityCards;
        this.fiveCardHand = new Card[5];
    }
}
