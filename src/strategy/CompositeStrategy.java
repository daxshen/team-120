package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;

//TODO add comment
abstract class CompositeStrategy extends PlayStrategy{
	// ------------------- Attributes ----------------------
	private ArrayList<Card> existingcCards = new ArrayList<>();
	private ArrayList<PlayStrategy> strategies = new ArrayList<>();
	
	
	// ------------------- Constructors --------------------
	public CompositeStrategy() {
		super();
	}

	// ------------------- Interface Method --------------------
	@Override
	//TODO add comment
	public Card execute(ArrayList<Card> hand, ArrayList<Card> trick, Suit trump, Suit lead) {
		return super.execute(hand, trick, trump, lead);
	}
}

class SmartStrategy extends CompositeStrategy{}
