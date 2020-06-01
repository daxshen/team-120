package strategy;

import player.Player;

/**
 * This class abstracts the selection of {@linkplain Strategy} by a
 * {@linkplain Player}
 */
public class StrategyFactory {
	// ------------------- Attributes ---------------------
	/** This singleton instance of {@linkplain StrategyFactory */
	private static StrategyFactory instance;

	// ------------------- Getters & Setters -------------------
	/** @return the singleton instance of {@linkplain StrategyFactory */
	public static StrategyFactory getInstance() {
		if (instance == null)
			instance = new StrategyFactory();
		return instance;
	}

	// ------------------- Methods ------------------------
	/**
	 * Creates and returns a {@linkplain Strategy} based on inputs
	 * 
	 * @param playStyle the {@linkplain Player}'s play style
	 */
	public Strategy getStrategy(String playStyle) {

		switch (playStyle.toUpperCase()) {
		case "RANDOM":
			return new RandomStrategy();

		case "LEGAL":
			return new LegalStrategy();

		case "SMART":
			return new SmartStrategy();

		/*
		 * case "MAXWIN": return HighestRankStrategy.getInstance();
		 * 
		 * case "MINLOSS": return LowestRankStrategy.getInstance();
		 * 
		 * case "SMART": return SmartStrategy.getInstance();
		 */

		default:
			return new RandomStrategy();
		}
	}
}
