package strategy;

import java.util.ArrayList;
import ch.aplu.jcardgame.Card;
import game.Poker.Suit;

/** This class is a type of {@linkplain Strategy} that combines multiple {@linkplain Strategy}s*/
abstract class CompositeStrategy extends LegalStrategy{
	
	// ------------------- Attributes ----------------------
	protected ArrayList<Card> previousCards = new ArrayList<>();
	protected ArrayList<Strategy> strategies = new ArrayList<>();
	
	
	// ------------------- Methods --------------------
	public void addStrategy(Strategy strategy) {
		strategies.add(strategy);
	}
}

//TODO Complete SmartStrategy logic
class SmartStrategy extends CompositeStrategy{
	
	// ------------------- Methods -------------------------
	@Override
	public Card execute(ArrayList<Card> playableCards, ArrayList<ArrayList<Card>> playedCards, Suit trump, Suit lead) {
		return null;
	}
}
