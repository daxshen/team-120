package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;


/** This class is a type of {@linkplain Strategy} that combines multiple {@linkplain Strategy}s*/
public class SmartStrategy extends Strategy{
	
	// ------------------- Attributes ----------------------
	private ArrayList<Strategy> strategies = new ArrayList<>();
	
	
	// ------------------- Methods --------------------
	public SmartStrategy(){
		addStrategy(new WinStrategy());
		addStrategy(new LoseStrategy());
	}
	public void addStrategy(Strategy strategy) {
		strategies.add(strategy);
	}
	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<ArrayList<Card>> previousTricks, Suit trump, Suit lead) {
		Card chosenCard = null;
		
		
		
		for(Strategy strategy: strategies) {
			chosenCard = strategy.execute(hand, previousTricks,trump,lead);
			if(chosenCard!=null)
				break;
		}
		
		return chosenCard;
	}
}

