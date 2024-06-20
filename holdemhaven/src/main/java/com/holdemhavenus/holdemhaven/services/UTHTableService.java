package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.entities.*;
import com.holdemhavenus.holdemhaven.repositories.DBHandRepository;
import com.holdemhavenus.holdemhaven.requestDTOs.PlayerActionRequest;
import com.holdemhavenus.holdemhaven.requestDTOs.SaveHandRequest;
import com.holdemhavenus.holdemhaven.responseDTOs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
//Service class used for handling actions regarding the UTH game functionality
//Also handles functionality of saving and retrieving hand histories from the dbHandRepository
public class UTHTableService implements TableGameService {
    @Autowired
    private DBHandRepository dbHandRepository;

    public UTHTable getTable() {
        return new UTHTable();
    }


    @Override
    //draws 4 cards from the deck, deals 2 to the player and 2 to the dealer
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
    //reveals the player's and dealer's hole cards and hank ranking
    //compares hand rankings to find the winner of the hand
    public ShowdownResponse showdown(UTHTable UTHTable) {
        HandService handService = new HandService();

        Hand playerHand = new Hand(UTHTable.getPlayerHoleCards(), UTHTable.getBoard());
        Hand dealerHand = new Hand(UTHTable.getDealerHoleCards(), UTHTable.getBoard());

        HandRanking playerHandRanking = handService.findHandRanking(playerHand);
        HandRanking dealerHandRanking = handService.findHandRanking(dealerHand);

        //compare player and dealer hand rankings
        if(playerHandRanking.getRanking() > dealerHandRanking.getRanking()) {
            return new ShowdownResponse('p', playerHandRanking.getRanking(), dealerHandRanking.getRanking(),
                    handService.toString(playerHand.getFiveCardHand(), playerHandRanking), handService.toString(dealerHand.getFiveCardHand(), dealerHandRanking));

        }
        else if (playerHandRanking.getRanking() < dealerHandRanking.getRanking()) {
            return new ShowdownResponse('d', playerHandRanking.getRanking(), dealerHandRanking.getRanking(),
                    handService.toString(playerHand.getFiveCardHand(), playerHandRanking), handService.toString(dealerHand.getFiveCardHand(), dealerHandRanking));
        }
        //if player and dealer have the same hand ranking, compare the five card hand, card by card, to determine who has the better hand
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
            //if five-card hands are exactly the same, it is a tie
            return new ShowdownResponse('t', playerHandRanking.getRanking(), dealerHandRanking.getRanking(),
                    handService.toString(playerHand.getFiveCardHand(), playerHandRanking), handService.toString(dealerHand.getFiveCardHand(), dealerHandRanking));
        }
    }

    @Override
    //resets table fields at the end of a hand
    public void endHand(UTHTable UTHTable) {
        UTHTable.getDeckService().joinDeck(UTHTable.getDeck());
        UTHTable.getBoard().clear();

        UTHTable.setPlayerHoleCards(new Card[2]);
        UTHTable.setDealerHoleCards(new Card[2]);
        UTHTable.setStreet("");
    }

    //handles player action after every street (pre-flop, flop, and river)
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

    //saves relevant hand information to the DBHand repository
    public SaveHandResponse saveHand(UTHTable UTHTable, SaveHandRequest request) {
        try {
            DBHand dbHand = new DBHand();
            dbHand.setPlayerId(request.getPlayerId());
            dbHand.setAnteBet(request.getAnteBet());
            dbHand.setDealerBet(request.getAnteBet());
            dbHand.setTripsBet(request.getTripsBet());
            dbHand.setPlayBet(request.getPlayBet());
            dbHand.setResult(request.getWinner());
            dbHand.setPlayerPayout(request.getPlayerPayout());
            dbHand.setPlayerHoleCards(UTHTable.getPlayerHoleCards()[0].toString()+UTHTable.getPlayerHoleCards()[1].toString());
            dbHand.setDealerHoleCards(UTHTable.getDealerHoleCards()[0].toString()+UTHTable.getDealerHoleCards()[1].toString());
            dbHand.setBoardCards(boardCardsToString(UTHTable.getBoard()));

            if(request.getWinner().equals("d")) dbHand.setResult("Dealer Wins");
            else if(request.getWinner().equals("p")) dbHand.setResult("Player Wins");
            else dbHand.setResult("Tie");

            dbHandRepository.save(dbHand);

            return new SaveHandResponse(true, "Successfully added hand to the database");
        } catch (Exception e) {
            return new SaveHandResponse(false, "Something went wrong saving the hand to the database");
        }
    }


    //returns latest 100 hands played by a given player
    public ArrayList<DBHand> getHandHistory(Long playerId) {
        return dbHandRepository.find100LatestHands(playerId);
    }

    //turns the five board cards into a readable string
    //for example, the 6 of diamonds is converted to "6d" and so on
    private String boardCardsToString(ArrayList<Card> cards) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < cards.size(); i++) {
            result.append(cards.get(i).toString());
        }

        return result.toString();
    }

    //handle pre-flop table action after user chooses an action (bet or check)
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

        //if the user bet, go to the next street
        if (request.getAction() == 'B') {
            return flopAction(UTHTable, request, response);
        }
        //if they check, return DTO
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

    //handle flop table action after user chooses an action (bet or check)
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

        //if the user bet, go to the next street
        if (request.getAction() == 'B') {
            return riverAction(UTHTable, request, response);
        }
        //if they check, return DTO
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

    //handle river table action after user chooses an action (bet or fold)
    private PlayerActionResponse riverAction(UTHTable UTHTable, PlayerActionRequest request, PlayerActionResponse response) {
        if (request.getAction() == 'B') {
            response.setDealerHoleCards(UTHTable.getDealerHoleCards());
            response.setSuccess(true);
            response.setMessage("Successful runout.");
            return response;
        }
        else if(request.getAction() == 'F') {
            response.setDealerHoleCards(UTHTable.getDealerHoleCards());
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
