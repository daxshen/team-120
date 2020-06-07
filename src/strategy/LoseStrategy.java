package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;
/** Class to implement {@linkplain Strategy} for choosing the best card to lose with */
public class LoseStrategy extends Strategy{
	
	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<ArrayList<Card>> previousTricks, Suit trump, Suit lead) {
		// All lowest possible cards following lead( to not break rule
		ArrayList<Card> lowestCards = getCardsOfSuit(hand, lead);
		
		//if no lead cards, then choose all non-trump cards
		if(lowestCards.isEmpty()) {
			for(Suit suit: Suit.values()) {
				if(suit != trump)
					lowestCards.addAll(getCardsOfSuit(hand, suit));
			}
		}
		
		//return the smallest ranked card of all loosing cards
		Card chosenCard = lowestRank(lowestCards);
		return chosenCard;
	}

}
