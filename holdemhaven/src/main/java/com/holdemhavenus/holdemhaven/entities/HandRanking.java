package com.holdemhavenus.holdemhaven.entities;

import lombok.Getter;

//Enumerable containing the ten major hand rankings in poker
@Getter
public enum HandRanking {
    ROYAL_FLUSH(9, "Royal Flush"),
    STRAIGHT_FLUSH(8, "High Straight Flush"),
    FOUR_OF_A_KIND(7, "Four of a Kind"),
    FULL_HOUSE(6, "Full House"),
    FLUSH(5, "High Flush"),
    STRAIGHT(4, "High Straight"),
    THREE_OF_A_KIND(3, "Three of a Kind"),
    TWO_PAIR(2, "Two Pair"),
    ONE_PAIR(1, "One Pair"),
    HIGH_CARD(0, "High");

    final int ranking;
    final String hand;

    HandRanking (int ranking, String hand) {
        this.ranking = ranking;
        this.hand = hand;
    }
}
