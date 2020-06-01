package player;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.Poker;
import strategy.StrategyFactory;

/** This class represents an computer player */
public class ComputerPlayer extends Player {
	String playStyle = null;

	/**
	 * Cards from all tricks played so far, including the previous tricks. The last
	 * element is all the cards from the current trick
	 */
	ArrayList<ArrayList<Card>> previousTricks = new ArrayList<>();

	// ------------------- Constructors -------------------
	public ComputerPlayer(int id, int thinkingTime, String playStyle) {
		super(id, thinkingTime);
		this.message = "Player " + id + " thinking...";
		this.playStyle = playStyle;
	}

	// ------------------- Methods ------------------------
	public Card playCard(Hand trick, Poker.Suit trump, Poker.Suit lead) {

		recordTricks(trick);

		/* A ComputerPlayer will use a Strategy to select the card */
		selectedCard = StrategyFactory.getInstance().getStrategy(playStyle).execute(hand.getCardList(), previousTricks,
				trump, lead);

		return super.playCard(trick, trump, lead);
	}

	/** Records the {@linkplain Card}s played by other {@linkplain Player}s */
	private void recordTricks(Hand trick) {

		if (!previousTricks.contains(trick)) {
			previousTricks.add(trick.getCardList());
		}

	}
}
