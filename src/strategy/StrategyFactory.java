package strategy;

//TODO add comment
public class StrategyFactory {
	// ------------------- Attributes ---------------------
	private static StrategyFactory instance;

	// ------------------- Getters & Setters -------------------
	public static StrategyFactory getInstance() {
		return instance;
	}

	// ------------------- Constructors -------------------
	public StrategyFactory() {
		this.instance = new StrategyFactory();
	}

	// ------------------- Methods ------------------------
	public PlayStrategy getStrategy(String type) {

		switch (type.toUpperCase()) {
		case "LEGAL":
			return new LegalStrategy();

		case "MAXWIN":
			return new MaxWinStrategy();

		case "MINLOSS":
			return new MinLossStrategy();

		case "SMART":
			return new SmartStrategy();

		default:
			return new PlayStrategy();
		}
	}
}
