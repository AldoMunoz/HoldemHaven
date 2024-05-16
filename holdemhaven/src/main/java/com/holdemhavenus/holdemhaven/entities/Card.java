package com.holdemhavenus.holdemhaven.entities;

public enum Card {
    TWO_S(2, 's', "/images/cards/TWO_S.png"),
    TWO_C(2, 'c', "/images/cards/TWO_C.png"),
    TWO_H(2, 'h', "/images/cards/TWO_H.png"),
    TWO_D(2, 'd', "/images/cards/TWO_D.png"),
    THREE_S(3, 's', "/images/cards/THREE_S.png"),
    THREE_C(3, 'c', "/images/cards/THREE_C.png"),
    THREE_H(3, 'h', "/images/cards/THREE_H.png"),
    THREE_D(3, 'd', "/images/cards/THREE_D.png"),
    FOUR_S(4, 's', "/images/cards/FOUR_S.png"),
    FOUR_C(4, 'c', "/images/cards/FOUR_C.png"),
    FOUR_H(4, 'h', "/images/cards/FOUR_H.png"),
    FOUR_D(4, 'd', "/images/cards/FOUR_D.png"),
    FIVE_S(5, 's', "/images/cards/FIVE_S.png"),
    FIVE_C(5, 'c', "/images/cards/FIVE_C.png"),
    FIVE_H(5, 'h', "/images/cards/FIVE_H.png"),
    FIVE_D(5, 'd', "/images/cards/FIVE_D.png"),
    SIX_S(6, 's', "/images/cards/SIX_S.png"),
    SIX_C(6, 'c', "/images/cards/SIX_C.png"),
    SIX_H(6, 'h', "/images/cards/SIX_H.png"),
    SIX_D(6, 'd', "/images/cards/SIX_D.png"),
    SEVEN_S(7, 's', "/images/cards/SEVEN_S.png"),
    SEVEN_C(7, 'c', "/images/cards/SEVEN_C.png"),
    SEVEN_H(7, 'h', "/images/cards/SEVEN_H.png"),
    SEVEN_D(7, 'd', "/images/cards/SEVEN_D.png"),
    EIGHT_S(8, 's', "/images/cards/EIGHT_S.png"),
    EIGHT_C(8, 'c', "/images/cards/EIGHT_C.png"),
    EIGHT_H(8, 'h', "/images/cards/EIGHT_H.png"),
    EIGHT_D(8, 'd',"/images/cards/EIGHT_D.png"),
    NINE_S(9, 's', "/images/cards/NINE_S.png"),
    NINE_C(9, 'c', "/images/cards/NINE_C.png"),
    NINE_H(9, 'h', "/images/cards/NINE_H.png"),
    NINE_D(9, 'd', "/images/cards/NINE_D.png"),
    TEN_S(10, 's', "/images/cards/TEN_S.png"),
    TEN_C(10, 'c', "/images/cards/TEN_C.png"),
    TEN_H(10, 'h', "/images/cards/TEN_H.png"),
    TEN_D(10, 'd', "/images/cards/TEN_D.png"),
    JACK_S(11, 's', "/images/cards/JACK_S.png"),
    JACK_C(11, 'c', "/images/cards/JACK_C.png"),
    JACK_H(11, 'h', "/images/cards/JACK_H.png"),
    JACK_D(11, 'd', "/images/cards/JACK_D.png"),
    QUEEN_S(12, 's', "/images/cards/QUEEN_S.png"),
    QUEEN_C(12, 'c', "/images/cards/QUEEN_C.png"),
    QUEEN_H(12, 'h', "/images/cards/QUEEN_H.png"),
    QUEEN_D(12, 'd', "/images/cards/QUEEN_D.png"),
    KING_S(13, 's', "/images/cards/KING_S.png"),
    KING_C(13, 'c', "/images/cards/KING_C.png"),
    KING_H(13, 'h', "/images/cards/KING_H.png"),
    KING_D(13, 'd', "/images/cards/KING_D.png"),
    ACE_S(14, 's', "/images/cards/ACE_S.png"),
    ACE_C(14, 'c', "/images/cards/ACE_C.png"),
    ACE_H(14, 'h', "/images/cards/ACE_H.png"),
    ACE_D(14, 'd', "/images/cards/ACE_D.png");

    private final int val;
    private final char suit;
    private final String imagePath;

    Card (int val, char suit, String imagePath) {
        this.val = val;
        this.suit = suit;
        this.imagePath = imagePath;
    }

    public int getVal() {
        return val;
    }

    public char getSuit() {
        return suit;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString() {
        if(this.val == 14) {
            return "A" + this.suit;
        }
        else if (this.val == 13) {
            return "K" + this.suit;
        }
        else if (val == 12) {
            return "Q" + this.suit;
        }
        else if (val == 11) {
            return "J" + this.suit;
        }
        else {
            return "" + this.val + this.suit;
        }
    }
}
