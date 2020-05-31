package player;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker;
import strategy.StrategyFactory;

//TODO add comment
public class ComputerPlayer extends Player {
	String playStyle = null;

	//------------------- Constructors -------------------
	public ComputerPlayer(int id, int thinkingTime, String playStyle) {
		super(id, thinkingTime);
		this.message = "Player " + id + " thinking...";
		this.playStyle = playStyle;
	}
	
	//------------------- Methods ------------------------
	//TODO add guard check where hand is empty	
	//TODO add comment
	public Card playCard(ArrayList<Card> trick, Poker.Suit trump, Poker.Suit lead) {
		selectedCard = StrategyFactory.getInstance().getStrategy(playStyle).execute(hand.getCardList(), trick, trump, lead).get(0);
		return super.playCard(trick, trump, lead);
	}
}

