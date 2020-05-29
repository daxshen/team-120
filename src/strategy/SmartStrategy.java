package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;

//TODO add comment
abstract class SmartStrategy extends PlayStrategy{
	
	// ------------------- Attributes ----------------------
	protected ArrayList<PlayStrategy> strategies = null;
	
	
	// ------------------- Constructors --------------------
	public SmartStrategy(ArrayList<Card> hand, ArrayList<Card> cardPool, Suit trump, Suit lead) {
		super(hand, cardPool, trump, lead);
		// TODO Auto-generated constructor stub
	}

	// ------------------- Methods -----------------------------
	@Override
	public Card playCard() {
		return super.playCard();
	}

}