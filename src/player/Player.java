package player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.Poker;

/** This class represents a player*/
abstract public class Player {
	//------------------- Attributes --------------------
	protected int id;
	protected int score = 0;
	
	/** The {@linkplain Hand} of a player that contains its {@linkplain Card}s*/
	protected Hand hand = null;
	
	/** The {@linkplain Card} that is selected to play*/
	protected Card selectedCard = null;
	
	/** The {@linkplain Player}'s status text*/
	protected String message = "";
	
	protected int thinkingTime;


	//------------------- Getters & Setters -------------------	
	public int getThinkingTime() {
		return thinkingTime;
	}

	public void setThinkingTime(int thinkingTime) {
		this.thinkingTime = thinkingTime;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public Hand getHand () {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Card getSelectedCard() {
		return selectedCard;
	}

	public void setSelectedCard(Card selectedCard) {
		this.selectedCard = selectedCard;
	}
	
	public boolean handEmpty() {
		
		return (hand.getCardList().size() == 0);
	}
	
	
	//------------------- Constructors -------------------
	/** Initialises the {@linkplain Player}
	 * @param id the ID of the player
	 * @param thinkingTime the time it takes the player to play a {@linkplain Card}*/
	public Player(int id, int thinkingTime) {	
		this.id = id;
		this.thinkingTime = thinkingTime;
	}
	
	
	//------------------- Methods ------------------------
	/** Selects and plays a {@linkplain Card}
	 * @param trick the cards played by other {@linkplain Player}s
	 * @param trump the trump's suit
	 * @param lead the leading card's suit
	 * @return the {@linkplain Card} selected to play*/
	public Card playCard(Hand trick, Poker.Suit trump, Poker.Suit lead) {
		if (selectedCard != null)
			System.out.println(" Player " + id + " : suit = " + selectedCard.getSuit() + ", rank = " + selectedCard.getRankId());
		return selectedCard;
	}
}
