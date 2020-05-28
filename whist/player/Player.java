package player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

abstract public class Player {
	//------------------- Attributes --------------------
	protected int id;
	protected int score = 0;
	protected Hand hand;
	protected Card selectedCard = null;
	protected String message;
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
	public Player(int id, Hand hand, int thinkingTime) {	
		this.id = id;
		this.hand = hand;
		this.thinkingTime = thinkingTime;
	}
	
	
	//------------------- Methods ------------------------
	//TODO add guard check where hand is empty
	public Card playCard() {
		return selectedCard;
		//Emulate thinking time
	}
	
}
