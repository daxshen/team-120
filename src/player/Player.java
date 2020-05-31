package player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.Poker;

//TODO add comment
abstract public class Player {
	//------------------- Attributes --------------------
	protected int id;
	protected int score = 0;
	protected Hand hand = null;
	protected Card selectedCard = null;
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
	public Player(int id, int thinkingTime) {	
		this.id = id;
		this.thinkingTime = thinkingTime;
	}
	
	
	//------------------- Methods ------------------------
	//TODO add guard check where hand is empty	
	//TODO add comment
	public Card playCard(Hand trick, Poker.Suit trump, Poker.Suit lead) {
		if (selectedCard != null)
			System.out.println(" Player " + id + " : suit = " + selectedCard.getSuit() + ", rank = " + selectedCard.getRankId());
		return selectedCard;
	}
}
