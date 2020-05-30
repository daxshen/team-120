package player;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ch.aplu.jcardgame.Card;
import game.Poker;
import strategy.PlayStrategy;

public class ComputerPlayer extends Player {
	private PlayStrategy playStrategy = null;

	//------------------- Constructors -------------------
	public ComputerPlayer(int id, int thinkingTime, String playStyle) {
		super(id, thinkingTime);
		this.message = "Player " + id + " thinking...";
		this.playStrategy = new PlayStrategy();
	}
	
	//------------------- Methods ------------------------
	//TODO add guard check where hand is empty	
	//TODO add comment
	public Card playCard(ArrayList<Card> trick, Poker.Suit trump, Poker.Suit lead) {
		selectedCard = playStrategy.execute(hand.getCardList(), trick, trump, lead);
		return super.playCard(trick, trump, lead);
	}
}

