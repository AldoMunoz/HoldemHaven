package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.entities.*;
import com.holdemhavenus.holdemhaven.requestDTOs.PlayerActionRequest;
import com.holdemhavenus.holdemhaven.responseDTOs.DealHandResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.PlayerActionResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.ShowdownResponse;
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
        table.getPlayerHoleCards()[0] = table.getDeckService().drawCard(table.getDeck());
        table.getPlayerHoleCards()[1] = table.getDeckService().drawCard(table.getDeck());

        //deal two cards to the dealer
        table.getDealerHoleCards()[0] = table.getDeckService().drawCard(table.getDeck());
        table.getDealerHoleCards()[1] = table.getDeckService().drawCard(table.getDeck());

        //set the current street to pre-flop
        table.setStreet("preFlop");

        //send the player cards back to the front-end
        DealHandResponse response = new DealHandResponse(table.getPlayerHoleCards());
        return response;
    }

    public ShowdownResponse showdown(Table table) {
        HandService handService = new HandService();

        Hand playerHand = new Hand(table.getPlayerHoleCards(), table.getBoard());
        Hand dealerHand = new Hand(table.getDealerHoleCards(), table.getBoard());

        HandRanking playerHandRanking = handService.findHandRanking(playerHand);
        HandRanking dealerHandRanking = handService.findHandRanking(dealerHand);

        if(playerHandRanking.getRanking() > dealerHandRanking.getRanking()) {
            return new ShowdownResponse('p', playerHandRanking.getRanking(), dealerHandRanking.getRanking(),
                    handService.toString(playerHand.getFiveCardHand(), playerHandRanking), handService.toString(dealerHand.getFiveCardHand(), dealerHandRanking));

        }
        else if (playerHandRanking.getRanking() < dealerHandRanking.getRanking()) {
            return new ShowdownResponse('d', playerHandRanking.getRanking(), dealerHandRanking.getRanking(),
                    handService.toString(playerHand.getFiveCardHand(), playerHandRanking), handService.toString(dealerHand.getFiveCardHand(), dealerHandRanking));
        }
        else {
            for (int i = 0; i < 5; i++) {
                if(playerHand.getFiveCardHand()[i].getVal() > dealerHand.getFiveCardHand()[i].getVal()) {
                    return new ShowdownResponse('p', playerHandRanking.getRanking(), dealerHandRanking.getRanking(),
                            handService.toString(playerHand.getFiveCardHand(), playerHandRanking), handService.toString(dealerHand.getFiveCardHand(), dealerHandRanking));
                }
                else if(playerHand.getFiveCardHand()[i].getVal() < dealerHand.getFiveCardHand()[i].getVal()) {
                    return new ShowdownResponse('d', playerHandRanking.getRanking(), dealerHandRanking.getRanking(),
                            handService.toString(playerHand.getFiveCardHand(), playerHandRanking), handService.toString(dealerHand.getFiveCardHand(), dealerHandRanking));
                }
            }
            return new ShowdownResponse('t', playerHandRanking.getRanking(), dealerHandRanking.getRanking(),
                    handService.toString(playerHand.getFiveCardHand(), playerHandRanking), handService.toString(dealerHand.getFiveCardHand(), dealerHandRanking));
        }
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
        Card boardCard1 = table.getDeckService().drawCard(table.getDeck());
        Card boardCard2 = table.getDeckService().drawCard(table.getDeck());
        Card boardCard3 = table.getDeckService().drawCard(table.getDeck());

        //add flop cards to the response
        response.getBoardCards().add(boardCard1);
        response.getBoardCards().add(boardCard2);
        response.getBoardCards().add(boardCard3);

        //add cards to the table board
        table.getBoard().add(boardCard1);
        table.getBoard().add(boardCard2);
        table.getBoard().add(boardCard3);

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
        Card boardCard1 = table.getDeckService().drawCard(table.getDeck());
        Card boardCard2 = table.getDeckService().drawCard(table.getDeck());

        //add turn and river cards to the response
        response.getBoardCards().add(boardCard1);
        response.getBoardCards().add(boardCard2);

        //add cards to the table board
        table.getBoard().add(boardCard1);
        table.getBoard().add(boardCard2);

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
