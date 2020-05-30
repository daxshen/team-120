package strategy;

import java.util.ArrayList;
import ch.aplu.jcardgame.Card;
import game.Poker.Suit;

//TODO add comment
abstract class CompositeStrategy extends PlayStrategy{
	// ------------------- Attributes ----------------------
	private ArrayList<Card> previousCards = new ArrayList<>();
	private ArrayList<PlayStrategy> strategies = new ArrayList<>();
	
	// ------------------- Constructors --------------------
	public CompositeStrategy() {
		super();
	}
	
	// ------------------- Methods --------------------
	public void addStrategy(PlayStrategy strategy) {
		strategies.add(strategy);
	}
}

//TODO add comment
class SmartStrategy extends CompositeStrategy{
	
	// ------------------- Constructors --------------------
	public SmartStrategy() {
		super();
	}
	
	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<Card> trick, Suit trump, Suit lead) {
		return null;
	}
}
