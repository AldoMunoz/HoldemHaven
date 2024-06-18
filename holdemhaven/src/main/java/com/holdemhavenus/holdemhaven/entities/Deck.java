package com.holdemhavenus.holdemhaven.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.EnumSet;

@Getter
@Setter
//Entity containing and managing the 52 cards
public class Deck {
    private ArrayList<Card> cards;
    private ArrayList<Card> deadCards;

    public Deck() {
        cards = new ArrayList<>(EnumSet.allOf(Card.class));
        deadCards = new ArrayList<>();
    }
}
