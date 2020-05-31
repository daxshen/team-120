package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker;
import game.Poker.Suit;

public class Calculator {
	// ------------------ Attributes -------------------
	private static Calculator instance;
	
	
	// ------------------- Getters & Setters -------------------
	//TODO add comment
	public static Calculator getInstance() {
		if (instance == null)
			instance =  new Calculator();
		return instance;
	}

	// ------------------ Utility Methods -------------------
	//TODO add comment
	public ArrayList<Card> getCardsOfSuit(ArrayList<Card> hand, Suit suit){
		ArrayList<Card> cardsofSuit = new ArrayList<>();
		for (Card card : hand) {
			if (card.getSuit() == suit)
				cardsofSuit.add(card);
		}
		return cardsofSuit;
	}
	
	//TODO add comment
	public boolean isLegal(ArrayList<Card> hand, ArrayList<Card> trick, Card card, Suit trump, Suit lead) {
		ArrayList<Card> legalCards = new ArrayList<>();
		for (Card handCard : hand) {
			if (handCard.getSuit() == lead || handCard.getSuit() == trump)
			legalCards.add(handCard);
		}
				
		// Can play any card when player is the lead or when no suitable card
		if (trick.size() == 0 || legalCards.size() == 0)
			return true;
		// must play either a card of the lead suit or trump suit
		else
			if (legalCards.contains(card))
				return true;
			else
				return false;

	}
	
	public Card lowestRank(ArrayList<Card> cards) {
		Card lowest = null;
		if (cards.size() > 0) {
			lowest = cards.get(0);
			for (int i = 1 ; i < cards.size(); i++) {
				if(!rankGreater(cards.get(i), lowest));
					lowest = cards.get(i);
			}
		}
		return lowest;
	}
	
	public Card highestRank(ArrayList<Card> cards) {
		Card highest = null;
		if (cards.size() > 0) {
			highest = cards.get(0);
			for (int i = 1 ; i < cards.size(); i++) {
				if(rankGreater(cards.get(i), highest));
					highest = cards.get(i);
			}
		}
		return highest;
	}
	
	// TODO add comment
	public Card winningCard(ArrayList<Card> cards, Poker.Suit lead, Poker.Suit trump) {
		Card winningCard = cards.get(0);
		
		for (int i = 1; i < cards.size(); i++) {
			Card card = cards.get(i);
			
			// beat current winner with higher card
			boolean sameSuit = (card.getSuit() == winningCard.getSuit());
			boolean rankGreater = rankGreater(card, winningCard);
			
			// trumped when non-trump was winning
			boolean isTrump = (card.getSuit() == trump);
			boolean winningCardNotTrump = (winningCard.getSuit() != trump);
			
			if ( (sameSuit && rankGreater) || (isTrump && winningCardNotTrump) ) {
				winningCard = card;
			}
		}
		return winningCard;
	}
	
	//TODO
	public boolean rankGreater(Card card1, Card card2) {
		return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
	}
}
