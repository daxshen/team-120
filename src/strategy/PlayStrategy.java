package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;


//TODO add comment
public class PlayStrategy implements IStrategy{

	// ------------------- Attributes ----------------------
	protected ArrayList<Card> hand;
	protected ArrayList<Card> existingCards;
	protected Suit trump;
	protected Suit lead;
	
	
	
	// ------------------- Constructors --------------------
	public PlayStrategy(
			ArrayList<Card> hand, 
			ArrayList<Card> cardPool, 
			Suit trump, 
			Suit lead) {
		
		this.hand = hand;
		this.existingCards = cardPool;
		this.trump = trump;
		this.lead = lead;
		
	}

	// ------------------- Methods -----------------------------
	//TODO add comment
	public Card playCard() {
		return null;
	}
	
	
	public Card playCard(
			ArrayList<Card> hand, 
			ArrayList<Card> cardPool, 
			Suit trump, 
			Suit lead) {
		
		return null;
	}
	
	// ------------------- Interface Method --------------------
	@Override
	public Card play() {	
		return playCard();
	}
}
