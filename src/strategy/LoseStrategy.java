package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;

public class LoseStrategy extends Strategy{
	
	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<ArrayList<Card>> previousTricks, Suit trump, Suit lead) {
		ArrayList<Card> lowestCards = getCardsOfSuit(hand, lead);
		
		if(lowestCards.isEmpty()) {
			for(Suit suit: Suit.values()) {
				if(suit != trump)
					lowestCards.addAll(getCardsOfSuit(hand, suit));
			}
		}
		Card chosenCard = lowestRank(lowestCards);
		System.out.println("Break 3:" + lowestCards.toString());
		return chosenCard;
	}

}
