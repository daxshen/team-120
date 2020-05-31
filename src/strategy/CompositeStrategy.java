package strategy;

import java.util.ArrayList;
import ch.aplu.jcardgame.Card;
import game.Poker.Suit;

//TODO add comment
abstract class CompositeStrategy extends LegalStrategy{
	
	// ------------------- Attributes ----------------------
	protected ArrayList<Card> previousCards = new ArrayList<>();
	protected ArrayList<Strategy> strategies = new ArrayList<>();
	
	// ------------------- Constructors --------------------
	public CompositeStrategy() {
		super();
	}
	
	// ------------------- Methods --------------------
	public void addStrategy(Strategy strategy) {
		strategies.add(strategy);
	}
}

//TODO add comment
class SmartStrategy extends CompositeStrategy{
	
	// ------------------- Attributes ----------------------
	private static SmartStrategy instance;
	
	// ------------------- Getters & Setters ---------------
	public static SmartStrategy getInstance() {
		if (instance == null) {
			instance = new SmartStrategy();
			return instance;
		}
		else
			return instance;
	}
	
	// ------------------- Constructors --------------------
	public SmartStrategy() {
		super();
	}
	
	// ------------------- Methods -------------------------
	@Override
	public Card execute(ArrayList<Card> playableCards, ArrayList<ArrayList<Card>> playedCards, Suit trump, Suit lead) {
		Card card = null;
		do {
			card = randomCard(playableCards);
		}while(!isLegal(playableCards, playedCards.get(playedCards.size() - 1), card, trump, lead));
		
		previousCards.add(card);
		
		System.out.println("SMART strat played: ");
		
		return card;
	}
}
