package player;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.Poker;
import strategy.StrategyFactory;

//TODO add comment
public class ComputerPlayer extends Player {
	String playStyle = null;
	ArrayList<ArrayList<Card>> previousTricks = new ArrayList<>(); 
	
	//------------------- Constructors -------------------
	public ComputerPlayer(int id, int thinkingTime, String playStyle) {
		super(id, thinkingTime);
		this.message = "Player " + id + " thinking...";
		this.playStyle = playStyle;
	}
	
	//------------------- Methods ------------------------
	//TODO add guard check where hand is empty	
	//TODO add comment
	public Card playCard(Hand trick, Poker.Suit trump, Poker.Suit lead) {
		
		recordTricks(trick);
		selectedCard = StrategyFactory.getInstance().getStrategy(playStyle)
				.execute(hand.getCardList(), previousTricks, trump, lead);
		return super.playCard(trick, trump, lead);
	}
	
	private void recordTricks(Hand trick) {
		
		if (!previousTricks.contains(trick)) {
			previousTricks.add(trick.getCardList());
		}
	
	}
}

