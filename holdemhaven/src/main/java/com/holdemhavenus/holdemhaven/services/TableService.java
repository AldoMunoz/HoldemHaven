package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.entities.Card;
import com.holdemhavenus.holdemhaven.entities.Deck;
import com.holdemhavenus.holdemhaven.entities.Table;
import com.holdemhavenus.holdemhaven.responseDTOs.DealHandResponse;
import org.springframework.stereotype.Service;

@Service
public class TableService {
    private Table table;
    private DeckService deckService;
    private Deck deck;

    public TableService() {
        this.table = new Table();
        this.deckService = new DeckService();
        this.deck = new Deck();
    }

    public Table getTable() {
        return new Table();
    }

    public void startNewGame(Table table) {
        //todo
    }


    //draws 4 cards from the deck, deals 2 to the player and 2 to the dealer
    public DealHandResponse dealHoleCards(Table table) {
        deckService.shuffleCards(deck);
        //deal two cards to the player
        table.getPlayerHoleCards().add(deckService.drawCard(deck));
        table.getPlayerHoleCards().add(deckService.drawCard(deck));

        //deal two cards to the dealer
        table.getDealerHoleCards().add(deckService.drawCard(deck));
        table.getDealerHoleCards().add(deckService.drawCard(deck));

        //send the player cards back to the front-end
        DealHandResponse response = new DealHandResponse(table.getPlayerHoleCards());
        return response;
    }
}
