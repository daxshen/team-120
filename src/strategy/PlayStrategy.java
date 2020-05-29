package strategy;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;


//TODO add comment
public class PlayStrategy implements IStrategy{

	// ------------------- Attributes ----------------------
/*	protected ArrayList<Card> hand;
	protected ArrayList<Card> trick;
	protected Suit trump;
	protected Suit lead;*/
	
	
	
	// ------------------- Constructors --------------------
/*	public PlayStrategy(
			ArrayList<Card> hand, 
			ArrayList<Card> cardPool, 
			Suit trump, 
			Suit lead) {
		
		this.hand = hand;
		this.trick = cardPool;
		this.trump = trump;
		this.lead = lead;
		
	}*/

	public PlayStrategy() {}
		
	// ------------------- Interface Method --------------------
	//TODO add comment
	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<Card> trick, Suit trump, Suit lead) {	
		//Default computer player will play a random card regardless of rules
		int i = ThreadLocalRandom.current().nextInt(hand.size());
		return hand.get(i);
	}
}
