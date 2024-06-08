package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.entities.Card;
import com.holdemhavenus.holdemhaven.entities.Deck;
import com.holdemhavenus.holdemhaven.entities.Player;
import com.holdemhavenus.holdemhaven.entities.Table;
import com.holdemhavenus.holdemhaven.requestDTOs.PlayerActionRequest;
import com.holdemhavenus.holdemhaven.responseDTOs.DealHandResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.PlayerActionResponse;
import org.springframework.stereotype.Service;

@Service
public class TableService {
    public Table getTable() {
        return new Table();
    }


    //draws 4 cards from the deck, deals 2 to the player and 2 to the dealer
    public DealHandResponse dealHoleCards(Table table) {
        table.getDeckService().shuffleCards(table.getDeck());
        //deal two cards to the player
        table.getPlayerHoleCards().add(table.getDeckService().drawCard(table.getDeck()));
        table.getPlayerHoleCards().add(table.getDeckService().drawCard(table.getDeck()));

        //deal two cards to the dealer
        table.getDealerHoleCards().add(table.getDeckService().drawCard(table.getDeck()));
        table.getDealerHoleCards().add(table.getDeckService().drawCard(table.getDeck()));

        //set the current street to pre-flop
        table.setStreet("preFlop");

        //send the player cards back to the front-end
        DealHandResponse response = new DealHandResponse(table.getPlayerHoleCards());
        return response;
    }

    public PlayerActionResponse playerAction(Table table, PlayerActionRequest request) {
        PlayerActionResponse response = new PlayerActionResponse();
        if(table.getStreet().equals("preFlop")) {
            response.setStreet('f');
            return preFlopAction(table, request, response);
        }
        else if(table.getStreet().equals("flop")) {
            response.setStreet('r');
            return flopAction(table, request, response);
        }
        else if(table.getStreet().equals("river")) {
            response.setStreet('e');
            return riverAction(table, request, response);
        }
        else {
            return new PlayerActionResponse(false, "Error occurred, street not set properly.");
        }
    }

    private PlayerActionResponse preFlopAction(Table table, PlayerActionRequest request, PlayerActionResponse response) {
        //deal flop
        response.getBoardCards().add(table.getDeckService().drawCard(table.getDeck()));
        response.getBoardCards().add(table.getDeckService().drawCard(table.getDeck()));
        response.getBoardCards().add(table.getDeckService().drawCard(table.getDeck()));

        if (request.getAction() == 'B') {
            return flopAction(table, request, response);
        }
        else if (request.getAction() == 'C') {
            response.setSuccess(true);
            response.setMessage("Dealt flop.");
            table.setStreet("flop");

            return response;
        }
        else {
            return new PlayerActionResponse(false, "Error, action not identifiable.");
        }
    }

    private PlayerActionResponse flopAction(Table table, PlayerActionRequest request, PlayerActionResponse response) {
        //deal turn and river
        response.getBoardCards().add(table.getDeckService().drawCard(table.getDeck()));
        response.getBoardCards().add(table.getDeckService().drawCard(table.getDeck()));

        if (request.getAction() == 'B') {
            return riverAction(table, request, response);
        }
        else if (request.getAction() == 'C') {
            response.setSuccess(true);
            response.setMessage("Dealt turn and river.");

            table.setStreet("river");

            return response;
        }
        else {
            return new PlayerActionResponse(false, "Error, action not identifiable.");
        }
    }

    private PlayerActionResponse riverAction(Table table, PlayerActionRequest request, PlayerActionResponse response) {
        if (request.getAction() == 'B') {
            response.setDealerHoleCards(table.getDealerHoleCards());
            response.setSuccess(true);
            response.setMessage("Successful runout.");
            return response;
        }
        else if (request.getAction() == 'F') {
            response.setSuccess(true);
            response.setMessage("Folded hand.");

            table.setStreet("");

            return response;
        }
        else {
            return new PlayerActionResponse(false, "Error, action not identifiable.");
        }
    }
}
