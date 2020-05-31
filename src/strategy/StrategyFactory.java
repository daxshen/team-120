package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker;
import game.Poker.Suit;

//TODO add comment
public class StrategyFactory {
	// ------------------- Attributes ---------------------
	private static StrategyFactory instance;
	
	// ------------------- Getters & Setters -------------------
	//TODO add comment
	public static StrategyFactory getInstance() {
		if (instance == null)
			instance =  new StrategyFactory();
		return instance;
	}

	//TODO add comment
	// ------------------- Methods ------------------------
	public static PlayStrategy getStrategy(String playStyle) {

		switch (playStyle.toUpperCase()) {
		case "RANDOM":
			return new PlayStrategy();

		case "LEGAL":
			return new LegalStrategy();

/*		case "MAXWIN":
			return HighestRankStrategy.getInstance();

		case "MINLOSS":
			return LowestRankStrategy.getInstance();

		case "SMART":
			return SmartStrategy.getInstance();

		default:
			return PlayStrategy.getInstance();*/
		}
		return null;
	}
}
