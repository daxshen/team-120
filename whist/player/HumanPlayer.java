package player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.CardListener;
import ch.aplu.jcardgame.Hand;

public class HumanPlayer extends Player {
	//------------------- Attributes -------------------------------

	//------------------- Getters & Setters ------------------------
	@Override
	public void setHand(Hand hand) {
		super.setHand(hand);
		CardListener cardListener = new CardAdapter() 
		{
			public void leftDoubleClicked(Card card) {
				selectedCard = card;
			}
		};
		this.hand.addCardListener(cardListener);
	}
	
	//------------------- Constructors -----------------------------
	public HumanPlayer(int id, Hand hand, int thinkingTime) {
		super(id, hand, thinkingTime);
		this.thinkingTime = 0;
		this.message = "Player " + id + " double-click on card to lead.";
		
		//Add listener to human player hand
		CardListener cardListener = new CardAdapter() 
		{
			public void leftDoubleClicked(Card card) {
				selectedCard = card;
			}
		};
		this.hand.addCardListener(cardListener);
	}
	
	//------------------- Methods ----------------------------------
	//TODO comment
	@Override
	public Card playCard() {
		hand.setTouchEnabled(true);
		return super.playCard();
	}

}
