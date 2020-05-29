import java.util.concurrent.ThreadLocalRandom;

/**
 * 
 */

/**
 * @author daxsh
 *
 */
public class Poker {

	public enum Suit {
		SPADES, HEARTS, DIAMONDS, CLUBS
	}

	public enum Rank {
		// Reverse order of rank importance (see rankGreater() below)
		// Order of cards is tied to card images
		ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
	}
	
	public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
		int x = ThreadLocalRandom.current().nextInt(clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[x];
	}
	
}
