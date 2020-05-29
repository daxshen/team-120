package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker.*;

//TODO add comment
class MinLossStrategy extends PlayStrategy{
	// ------------------- Attributes ----------------------

	// ------------------- Constructors --------------------
	public MinLossStrategy(ArrayList<Card> hand, ArrayList<Card> cardPool, Suit trump, Suit lead) {
		super(hand, cardPool, trump, lead);
		// TODO Auto-generated constructor stub
	}

	// ------------------- Methods -------------------------
	public Card playCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Suit trump, Suit lead, int score) {
		return null;
	}

}