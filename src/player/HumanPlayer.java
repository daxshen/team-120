package player;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.CardListener;
import ch.aplu.jcardgame.Hand;
import game.Poker;
import game.Poker.Suit;
import strategy.StrategyFactory;

//TODO add comment
public class HumanPlayer extends Player {
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
	public HumanPlayer(int id) {
		super(id, 0);
		this.thinkingTime = 0;
		this.message = "Player " + id + " double-click on card to lead.";
	}
	
	//------------------- Methods ----------------------------------
	//TODO comment
	@Override
	public Card playCard(ArrayList<Card> trick, Poker.Suit trump, Poker.Suit lead) {
		hand.setTouchEnabled(true);

		if (!isLegal(hand.getCardList(), trick, selectedCard, trump, lead)) 
				selectedCard = null;
		return super.playCard(trick, trump, lead);
	}

	//TODO
	public boolean isLegal(ArrayList<Card> hand, ArrayList<Card> trick, Card card, Suit trump, Suit lead) {
		ArrayList<Card> legalCards = new ArrayList<>();
		for (Card handCard : hand) {
			if (handCard.getSuit() == lead || handCard.getSuit() == trump)
			legalCards.add(handCard);
		}
				
		// Can play any card when player is the lead
		if (trick.size() == 0)
			return true;
		
		// must play either a card of the lead suit or trump suit
		else if (legalCards.size() > 0)
			if (legalCards.contains(card))
				return true;
			else
				return false;
		
		//If no suitable card, play any card
		else
			return true;
	}
	
}
