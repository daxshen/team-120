package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;

// TODO add comment
class MaxWinStrategy extends PlayStrategy{

	// ------------------- Constructors --------------------
	public MaxWinStrategy(ArrayList<Card> hand, ArrayList<Card> cardPool, Suit trump, Suit lead) {
		super(hand, cardPool, trump, lead);
		// TODO Auto-generated constructor stub
	}
	
	// ------------------- Methods -------------------------
	public Card playCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Suit trump, Suit lead) {
		return null;
	}

}