package player;


import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class Player {
	/*----------Attributes--------------*/
	
	private Hand hand;
	private Card selectedCard;
	
	/*--------Getters & Setters----------------*/
	
	public Hand getHand () {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}
	
	/*---------Constructors---------------*/
	
	public Player(Hand hand) {	
		
	}
	
	/*---------Methods---------------*/
	
	public Card playCard() {
		
		return selectedCard;
	}
}
