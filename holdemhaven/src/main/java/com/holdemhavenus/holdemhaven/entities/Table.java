package com.holdemhavenus.holdemhaven.entities;

import com.holdemhavenus.holdemhaven.services.DeckService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Table {
    private Deck deck;

    private ArrayList<Card> board;
    private ArrayList<Card> dealerHoleCards;
    private ArrayList<Card> playerHoleCards;

    public Table() {
        deck = new Deck();
        board = new ArrayList<>();
        dealerHoleCards = new ArrayList<>();
        playerHoleCards = new ArrayList<>();
    }
}
