package com.holdemhavenus.holdemhaven.entities;

import com.holdemhavenus.holdemhaven.services.DeckService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class UTHTable {
    private Deck deck;
    private DeckService deckService;
    private ArrayList<Card> board;
    private Card[] dealerHoleCards;
    private Card[] playerHoleCards;

    private String street;

    public UTHTable() {
        deck = new Deck();
        deckService = new DeckService();
        board = new ArrayList<>();
        dealerHoleCards = new Card[2];
        playerHoleCards = new Card[2];

        street = "";
    }
}
