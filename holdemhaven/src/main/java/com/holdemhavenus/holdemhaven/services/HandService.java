package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.entities.Card;
import com.holdemhavenus.holdemhaven.entities.Hand;
import com.holdemhavenus.holdemhaven.entities.HandRanking;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class HandService {
    public HandRanking findHandRanking(Hand hand) {
        //Creates array of cards, adds player cards and community cards to the array
        Card[] cards = new Card[hand.getHoleCards().length+hand.getCommunityCards().size()];
        cards[0] = hand.getHoleCards()[0];
        cards[1] = hand.getHoleCards()[1];
        for (int i = 2; i < hand.getCommunityCards().size()+2; i++) {
            cards[i] = hand.getCommunityCards().get(i-2);
        }

        //Sorts the cards from Hi->Lo
        reverseInsertionSort(cards);

        //Goes through hand ranks from hi->lo to find the best possible hand
        if (isRoyalFlush(hand, cards)) {
            hand.setHandRanking(HandRanking.ROYAL_FLUSH);
            return hand.getHandRanking();
        }
        else if(isStraightFlush(hand, cards)) {
            hand.setHandRanking(HandRanking.STRAIGHT_FLUSH);
            return hand.getHandRanking();
        }
        else if (isQuads(hand, cards)) {
            hand.setHandRanking(HandRanking.FOUR_OF_A_KIND);
            return hand.getHandRanking();
        }
        else if (isFullHouse(hand, cards)) {
            hand.setHandRanking(HandRanking.FULL_HOUSE);
            return hand.getHandRanking();
        }
        else if (isFlush(hand, cards)) {
            hand.setHandRanking(HandRanking.FLUSH);
            return hand.getHandRanking();
        }
        else if (isStraight(hand, cards)) {
            hand.setHandRanking(HandRanking.STRAIGHT);
            return hand.getHandRanking();
        }
        else if (isThreeOfAKind(hand, cards)) {
            hand.setHandRanking(HandRanking.THREE_OF_A_KIND);
            return hand.getHandRanking();
        }
        else if(isTwoPair(hand, cards)) {
            hand.setHandRanking(HandRanking.TWO_PAIR);
            return hand.getHandRanking();
        }
        else if (isPair(hand, cards)) {
            hand.setHandRanking(HandRanking.ONE_PAIR);
            return hand.getHandRanking();
        }
        else {
            isHighCard(hand, cards);
            hand.setHandRanking(HandRanking.HIGH_CARD);
            return hand.getHandRanking();
        }
    }

    //Sorts the cards using insertion sort in descending order
    private void reverseInsertionSort (Card[] cards) {
        for (int i = 1; i < cards.length; i++) {
            Card key = cards[i];
            int j = i - 1;

            // Shift elements to the left until the correct position for the key is found
            while (j >= 0 && cards[j].ordinal() < key.ordinal()) {
                cards[j + 1] = cards[j];
                j--;
            }
            cards[j + 1] = key;
        }
    }

    private boolean isRoyalFlush(Hand hand, Card[] cards) {
        //checks is a flush exists, and if the last card of that flush is a 10
        if(isFlush(hand, cards) && hand.getFiveCardHand()[4].getVal() == 10) return true;
        return false;
    }

    private boolean isStraightFlush(Hand hand, Card[] cards) {
        //checks is flush exists
        if(isFlush(hand, cards));
        else return false;

        //checks edge case for a wheel straight flush
        if (hand.getFiveCardHand()[0].getVal() == 14 && hand.getFiveCardHand()[1].getVal() == 5) {
            //if true, shifts all items to left
            //[A,5,4,3,2] -> [5,4,3,2,A]
            Card temp = hand.getFiveCardHand()[0];
            for (int i = 1; i < hand.getFiveCardHand().length; i++) {
                hand.getFiveCardHand()[i-1] = hand.getFiveCardHand()[i];
            }
            hand.getFiveCardHand()[hand.getFiveCardHand().length-1] = temp;

            return true;
        }
        //else, check for normal straight flush
        else {
            //checks if the 5 cards in the flush are in descending order
            for (int i = 1; i < hand.getFiveCardHand().length; i++) {
                if(hand.getFiveCardHand()[i].getVal()+1 == hand.getFiveCardHand()[i-1].getVal());
                    //if card is not 1 less than previous, return false
                else return false;
            }
            return true;
        }
    }

    private boolean isQuads(Hand hand, Card[] cards) {
        int count = 1;
        int currVal = cards[0].getVal();
        Card[] quads = new Card[5];

        for (int i = 1; i < cards.length; i++) {
            //first if case is when quads are found
            if(cards[i].getVal() == currVal && count == 3) {
                quads[0] = cards[i-count];
                quads[1] = cards[i-count+1];
                quads[2] = cards[i-count+2];
                quads[3] = cards[i];
                //following conditions find the high card (will either be first card or next card after quads
                if(cards[0].getVal() != currVal) quads[4] = cards[0];
                else quads[4] = cards[i+1];
                hand.setFiveCardHand(quads);
                return true;
            }
            //increases count if same value card is found
            else if(cards[i].getVal() == currVal && count < 3) count++;
                //current card is not the same as previous, count is reset
            else {
                currVal = cards[i].getVal();
                count = 1;
            }
        }
        return false;
    }

    private boolean isFullHouse(Hand hand, Card[] cards) {
        //check if three of a kind exists
        if(isThreeOfAKind(hand, cards));
        else return false;

        int tripVal = hand.getFiveCardHand()[0].getVal();
        int pairVal = 0;
        //looks for a pair
        for (int i = 0; i < cards.length; i++) {
            //when we encounter the trip value, we skip them by adding 2+1 to i
            if (cards[i].getVal() == tripVal) i += 2;
                //finds the first non-trip value card
            else if (pairVal == 0) {
                pairVal = cards[i].getVal();
            }
            //if a pair is found, they are made the 4th and 5th cards of the hand and returned true
            else if(cards[i].getVal() == pairVal) {
                changeFiveCardHandVal(hand,3, cards[i-1]);
                changeFiveCardHandVal(hand,4, cards[i]);
                return true;
            }
            //if the pair card does not match the current card, the pair card is set to the current card
            else {
                pairVal = cards[i].getVal();
            }
        }
        return false;
    }

    private boolean isFlush (Hand hand, Card[] cards) {
        //arrayList is used since the size of the list is unknown and changing
        ArrayList<Card> sFlush = new ArrayList<>();
        ArrayList<Card> cFlush = new ArrayList<>();
        ArrayList<Card> hFlush = new ArrayList<>();
        ArrayList<Card> dFlush = new ArrayList<>();
        Card[] ans = new Card[0];

        //counts each suit, returns true when 5 of the same suit is found
        for (Card card : cards) {
            if (card.getSuit() == 's') {
                sFlush.add(card);
                if (sFlush.size() == 5) {
                    hand.setFiveCardHand(sFlush.toArray(ans));
                    return true;
                }
            } else if (card.getSuit() == 'c') {
                cFlush.add(card);
                if (cFlush.size() == 5) {
                    hand.setFiveCardHand(cFlush.toArray(ans));
                    return true;
                }
            } else if (card.getSuit() == 'h') {
                hFlush.add(card);
                if (hFlush.size() == 5) {
                    hand.setFiveCardHand(hFlush.toArray(ans));
                    return true;
                }
            } else {
                dFlush.add(card);
                if (dFlush.size() == 5) {
                    hand.setFiveCardHand(dFlush.toArray(ans));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isStraight(Hand hand, Card[] cards) {
        int count = 1;
        Card[] straight = new Card[5];
        straight[0] = cards[0];

        for (int i = 1; i < cards.length; i++) {
            //if the current card is equal to the previous, we will ignore this card
            if(cards[i].getVal() == cards[i-1].getVal()) continue;
                //checks if the current card is one less than the previous one
            else if(cards[i].getVal()+1 == cards[i-1].getVal()) {
                straight[count] = cards[i];
                count++;
                //5 cards in a row have been found
                if(count == 5) {
                    hand.setFiveCardHand(straight);
                    return true;
                }
            }
            //if thd current card is not one less than the previous, the array is reinitialized and count is set back to 1
            else {
                straight = new Card[5];
                straight[0] = cards[i];
                count = 1;
            }
        }

        //edge case, checks for wheel
        if(count == 4 && cards[0].getVal() == 14 && cards[cards.length-1].getVal() == 2) {
            straight[count] = cards[0];
            hand.setFiveCardHand(straight);
            return true;
        }
        return false;
    }

    private boolean isThreeOfAKind(Hand hand, Card[] cards) {
        int count = 1;
        int tripVal = cards[0].getVal();

        //looks for the first instance of three of the same value
        for (int i = 1; i < cards.length; i++) {
            //when found, the three of a kind is added to the first 3 positions of final hand
            if(cards[i].getVal() == tripVal && count == 2) {
                Card[] threeOfAKind = new Card[5];
                threeOfAKind[0] = cards[i-2];
                threeOfAKind[1] = cards[i-1];
                threeOfAKind[2] = cards[i];

                //the last two positions, which will be high cards, are determined by iterating the list again
                int pos = 3;
                for (int j = 0; pos < 5; j++) {
                    //when we encounter the trip value, we skip them by adding 2+1 to j
                    if (cards[j].getVal() == tripVal) j += 2;
                        //if the current value isn't the trip value, it gets added to the five card hand
                    else {
                        threeOfAKind[pos] = cards[j];
                        pos++;
                    }
                }
                hand.setFiveCardHand(threeOfAKind);
                return true;
            }
            //this finds a pair
            else if(cards[i].getVal() == tripVal && count == 1) count++;
                //if the next card is not equal to the previous, a new tripVal is set
            else {
                tripVal = cards[i].getVal();
                count = 1;
            }
        }
        return false;
    }

    private boolean isTwoPair(Hand hand, Card[] cards) {
        int pairCount = 0;
        int currVal = cards[0].getVal();
        Card[] twoPair = new Card[5];

        for (int i = 1; i < cards.length; i++) {
            //finds the first pair, adds them to final hand
            if(cards[i].getVal() == currVal && pairCount == 0) {
                twoPair[0] = cards[i-1];
                twoPair[1] = cards[i];
                pairCount++;
            }
            //finds the second pair, adds them to final hand
            else if (cards[i].getVal() == currVal && pairCount == 1) {
                twoPair[2] = cards[i-1];
                twoPair[3] = cards[i];
                pairCount++;
                break;
            }
            else currVal = cards[i].getVal();
        }

        //adds the 5th high card if 2 pair is found
        if (pairCount == 2) {
            for (int i = 0; i < cards.length; i++) {
                if(cards[i].getVal() != twoPair[0].getVal() && cards[i].getVal() != twoPair[2].getVal()) {
                    twoPair[4] = cards[i];
                    hand.setFiveCardHand(twoPair);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPair(Hand hand, Card[] cards) {
        int currVal = cards[0].getVal();

        for (int i = 1; i < cards.length; i++) {
            //looks for the first instance of a matching card
            if(cards[i].getVal() == currVal) {
                Card[] pair = new Card[5];
                pair[0] = cards[i-1];
                pair[1] = cards[i];

                //fills in the hand with 3 high cards
                int pos = 2;
                for (int j = 0; pos < 5; j++) {
                    if(cards[j].getVal() == currVal) j++;
                    else {
                        pair[pos] = cards[j];
                        pos++;
                    }
                }
                hand.setFiveCardHand(pair);
                return true;
            }
            else currVal = cards[i].getVal();
        }
        return false;
    }

    //returns the 5 highest cards
    private void isHighCard(Hand hand, Card[] cards) {
        hand.setFiveCardHand(Arrays.copyOfRange(cards, 0, 5));
    }

    //used for isFullHouse, might get rid of later
    private void changeFiveCardHandVal(Hand hand, int pos, Card card) {
        hand.getFiveCardHand()[pos] = card;
    }

    //Converts a hand into a legible string
    public String toString(Card[] fiveCardHand, HandRanking handRanking) {
        String rank = handRanking.getHand() + ", ";
        ArrayList<String> hand = new ArrayList<>();
        for (Card c : fiveCardHand) {
            switch (c.getVal()) {
                case 2 -> hand.add("2");
                case 3 -> hand.add("3");
                case 4 -> hand.add("4");
                case 5 -> hand.add("5");
                case 6 -> hand.add("6");
                case 7 -> hand.add("7");
                case 8 -> hand.add("8");
                case 9 -> hand.add("9");
                case 10 -> hand.add("10");
                case 11 -> hand.add("J");
                case 12 -> hand.add("Q");
                case 13 -> hand.add("K");
                case 14 -> hand.add("A");
            }
        }
        return switch (rank) {
            case "High, " -> hand.get(0) + " High";
            case "One Pair, ", "Three of a Kind, ", "Four of a Kind, " -> rank + hand.get(0) + "'s";
            case "Two Pair, " -> rank + hand.get(0) + "'s & " + hand.get(2) + "'s";
            case "High Straight, " -> hand.get(0) + " High Straight";
            case "High Flush, " -> hand.get(0) + " High Flush";
            case "Full House, " -> rank + hand.get(0) + "'s full of " + hand.get(3) + "'s";
            case "High Straight Flush, " -> hand.get(0) + " High Straight Flush";
            case "Royal Flush, " -> "Royal Flush";
            default -> null;
        };
    }
}
