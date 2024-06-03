package com.holdemhavenus.holdemhaven.entities;

import com.holdemhavenus.holdemhaven.services.DeckService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Table {
    private Deck deck;
    private DeckService deckService;
    private ArrayList<Card> board;
    private ArrayList<Card> dealerHoleCards;
    private ArrayList<Card> playerHoleCards;

}
