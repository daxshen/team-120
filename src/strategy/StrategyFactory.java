package strategy;

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
	public PlayStrategy getStrategy(String type) {

		switch (type.toUpperCase()) {
		case "Random":
			return PlayStrategy.getInstance();

		case "LEGAL":
			return LegalStrategy.getInstance();

		case "MAXWIN":
			return HighestRankStrategy.getInstance();

		case "MINLOSS":
			return LowestRankStrategy.getInstance();

		case "SMART":
			return SmartStrategy.getInstance();

		default:
			return PlayStrategy.getInstance();
		}
	}
}
