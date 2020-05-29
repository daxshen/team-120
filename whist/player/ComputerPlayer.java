package player;

import java.util.concurrent.ThreadLocalRandom;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class ComputerPlayer extends Player {
	//------------------- Attribute
	
	//------------------- Constructors -------------------
	public ComputerPlayer(int id, Hand hand, int thinkingTime) {
		super(id, hand, thinkingTime);
		this.message = "Player " + id + " thinking...";

	}
	
	
	//------------------- Methods ------------------------
	//TODO A default player will play any random card
	public Card playCard() {
		selectedCard = randomCard(hand);
		return super.playCard();
	}
	
    public static Card randomCard(Hand hand){
        int x = ThreadLocalRandom.current().nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }
    
	//------------------- Methods ------------------------
    //asdasdasdasdasdasdasdasdasdasdasd
    //asdasdasdasdsad
}
