package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;

//TODO add comment
public class StrategyFactory {
	// ------------------- Attributes ---------------------
	private static StrategyFactory instance;
	private ArrayList<Card> previousCards = new ArrayList<>();
	
	// ------------------- Getters & Setters -------------------
	//TODO add comment
	public static StrategyFactory getInstance() {
		if (instance == null)
			instance =  new StrategyFactory();
		return instance;
	}

	//TODO add comment
	// ------------------- Methods ------------------------
	public Strategy getStrategy(String playStyle) {

		switch (playStyle.toUpperCase()) {
		case "RANDOM":
			return new RandomStrategy();

		case "LEGAL":
			return new LegalStrategy();

		case "SMART":
			return new SmartStrategy();
			
/*		case "MAXWIN":
			return HighestRankStrategy.getInstance();

		case "MINLOSS":
			return LowestRankStrategy.getInstance();

		case "SMART":
			return SmartStrategy.getInstance();*/
			
		default: 
			return new RandomStrategy();
		}
	}
}
