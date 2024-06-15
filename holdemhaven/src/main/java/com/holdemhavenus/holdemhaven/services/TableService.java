package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.entities.*;
import com.holdemhavenus.holdemhaven.requestDTOs.PlayerActionRequest;
import com.holdemhavenus.holdemhaven.responseDTOs.DealHandResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.GetDealerHandResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.PlayerActionResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.ShowdownResponse;
import org.springframework.stereotype.Service;

@Service
public class TableService implements TableGameService {
    public UTHTable getTable() {
        return new UTHTable();
    }


    //draws 4 cards from the deck, deals 2 to the player and 2 to the dealer
    @Override
    public DealHandResponse dealHoleCards(UTHTable UTHTable) {
        UTHTable.getDeckService().shuffleCards(UTHTable.getDeck());
        //deal two cards to the player
        UTHTable.getPlayerHoleCards()[0] = UTHTable.getDeckService().drawCard(UTHTable.getDeck());
        UTHTable.getPlayerHoleCards()[1] = UTHTable.getDeckService().drawCard(UTHTable.getDeck());

        //deal two cards to the dealer
        UTHTable.getDealerHoleCards()[0] = UTHTable.getDeckService().drawCard(UTHTable.getDeck());
        UTHTable.getDealerHoleCards()[1] = UTHTable.getDeckService().drawCard(UTHTable.getDeck());

        //set the current street to pre-flop
        UTHTable.setStreet("preFlop");

        //send the player cards back to the front-end
        return new DealHandResponse(UTHTable.getPlayerHoleCards());
    }

    @Override
    public ShowdownResponse showdown(UTHTable UTHTable) {
        HandService handService = new HandService();

        Hand playerHand = new Hand(UTHTable.getPlayerHoleCards(), UTHTable.getBoard());
        Hand dealerHand = new Hand(UTHTable.getDealerHoleCards(), UTHTable.getBoard());

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

    @Override
    public GetDealerHandResponse getDealerHand(UTHTable UTHTable) {
        HandService handService = new HandService();

        Hand dealerHand = new Hand(UTHTable.getDealerHoleCards(), UTHTable.getBoard());
        HandRanking dealerHandRanking = handService.findHandRanking(dealerHand);

        return new GetDealerHandResponse(UTHTable.getDealerHoleCards(), handService.toString(dealerHand.getFiveCardHand(), dealerHandRanking));
    }

    @Override
    public void endHand(UTHTable UTHTable) {
        UTHTable.getDeckService().joinDeck(UTHTable.getDeck());
        UTHTable.getBoard().clear();

        UTHTable.setPlayerHoleCards(new Card[2]);
        UTHTable.setDealerHoleCards(new Card[2]);
        UTHTable.setStreet("");
    }

    public PlayerActionResponse playerAction(UTHTable UTHTable, PlayerActionRequest request) {
        PlayerActionResponse response = new PlayerActionResponse();
        if(UTHTable.getStreet().equals("preFlop")) {
            response.setStreet('f');
            return preFlopAction(UTHTable, request, response);
        }
        else if(UTHTable.getStreet().equals("flop")) {
            response.setStreet('r');
            return flopAction(UTHTable, request, response);
        }
        else if(UTHTable.getStreet().equals("river")) {
            response.setStreet('e');
            return riverAction(UTHTable, request, response);
        }
        else {
            return new PlayerActionResponse(false, "Error occurred, street not set properly.");
        }
    }

    private PlayerActionResponse preFlopAction(UTHTable UTHTable, PlayerActionRequest request, PlayerActionResponse response) {
        //deal flop
        Card boardCard1 = UTHTable.getDeckService().drawCard(UTHTable.getDeck());
        Card boardCard2 = UTHTable.getDeckService().drawCard(UTHTable.getDeck());
        Card boardCard3 = UTHTable.getDeckService().drawCard(UTHTable.getDeck());

        //add flop cards to the response
        response.getBoardCards().add(boardCard1);
        response.getBoardCards().add(boardCard2);
        response.getBoardCards().add(boardCard3);

        //add cards to the table board
        UTHTable.getBoard().add(boardCard1);
        UTHTable.getBoard().add(boardCard2);
        UTHTable.getBoard().add(boardCard3);

        if (request.getAction() == 'B') {
            return flopAction(UTHTable, request, response);
        }
        else if (request.getAction() == 'C') {
            response.setSuccess(true);
            response.setMessage("Dealt flop.");
            UTHTable.setStreet("flop");

            return response;
        }
        else {
            return new PlayerActionResponse(false, "Error, action not identifiable.");
        }
    }

    private PlayerActionResponse flopAction(UTHTable UTHTable, PlayerActionRequest request, PlayerActionResponse response) {
        //deal turn and river
        Card boardCard1 = UTHTable.getDeckService().drawCard(UTHTable.getDeck());
        Card boardCard2 = UTHTable.getDeckService().drawCard(UTHTable.getDeck());

        //add turn and river cards to the response
        response.getBoardCards().add(boardCard1);
        response.getBoardCards().add(boardCard2);

        //add cards to the table board
        UTHTable.getBoard().add(boardCard1);
        UTHTable.getBoard().add(boardCard2);

        if (request.getAction() == 'B') {
            return riverAction(UTHTable, request, response);
        }
        else if (request.getAction() == 'C') {
            response.setSuccess(true);
            response.setMessage("Dealt turn and river.");

            UTHTable.setStreet("river");

            return response;
        }
        else {
            return new PlayerActionResponse(false, "Error, action not identifiable.");
        }
    }

    private PlayerActionResponse riverAction(UTHTable UTHTable, PlayerActionRequest request, PlayerActionResponse response) {
        if (request.getAction() == 'B') {
            response.setDealerHoleCards(UTHTable.getDealerHoleCards());
            response.setSuccess(true);
            response.setMessage("Successful runout.");
            return response;
        }
        else if (request.getAction() == 'F') {
            response.setSuccess(true);
            response.setMessage("Folded hand.");

            UTHTable.setStreet("");

            return response;
        }
        else {
            return new PlayerActionResponse(false, "Error, action not identifiable.");
        }
    }
}
