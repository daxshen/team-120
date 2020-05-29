package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;

//TODO add comment
abstract class SmartStrategy extends PlayStrategy{
	// ------------------- Attributes ----------------------
	private ArrayList<Card> existingcCards = new ArrayList<>();
	private ArrayList<PlayStrategy> strategies = new ArrayList<>();
	
	
	// ------------------- Constructors --------------------
	public SmartStrategy() {
		super();
	}

	// ------------------- Interface Method --------------------
	@Override
	//TODO add comment
	public Card execute(ArrayList<Card> hand, ArrayList<Card> trick, Suit trump, Suit lead) {
		return super.execute(hand, trick, trump, lead);
	}

}