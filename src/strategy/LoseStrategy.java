package strategy;

import java.util.ArrayList;
import java.util.HashMap;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;

public class LoseStrategy extends Strategy{
	
	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<ArrayList<Card>> previousTricks, Suit trump, Suit lead) {
		ArrayList<Card> lowestCards = new ArrayList<>();
		for(Card card: hand) {
			if(card.getSuit() == lead)
				lowestCards.add(card);
		}
		if(lowestCards.isEmpty()) {
			System.out.println("OUT OF LEAD CARDS");
			for(Card card: hand) {
				if(card.getSuit() != trump)
					lowestCards.add(card);
			}
		}
		System.out.println("Break 3: " + lowestCards.toString());
		Card chosenCard = lowestCards.get(0);
		for(Card card: lowestCards) {
			if(card.getRankId() > chosenCard.getRankId()) //change
				chosenCard = card;
		}
		return chosenCard;
	}

}