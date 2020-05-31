package strategy;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import ch.aplu.jcardgame.Card;
import game.Poker;
import game.Poker.Suit;

//TODO add comment
public class PlayStrategy implements IStrategy {	
	// ------------------- Interface Method --------------------
	// TODO add comment
	@Override
	public Card execute(ArrayList<Card> playableCards, ArrayList<Card> playedCards, Suit trump, Suit lead) {
		// Default computer player will play a random card regardless of rules
		int i = ThreadLocalRandom.current().nextInt(playableCards.size());
		return playableCards.get(i);
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
		
		// Can play any card when player is the lead or when no suitable card
		if (lead == null)
			return true;
		
		// Find the list of allowed-to-play cards
		ArrayList<Card> legalCards = new ArrayList<>();
		for (Card handCard : hand) {
			if (handCard.getSuit() == lead || handCard.getSuit() == trump)
				legalCards.add(handCard);
		}
				
		// If no legal cards, play any card
		if (legalCards.size() == 0)
			return true;
		
		// If there are legal cards when the selected card is not among them, return false
		if (!legalCards.contains(card))
			return false;
		// If the card is among the legal cards, return true, meaning it's legal
		else
			return true;
	}
	
	//TODO add comment
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
	
	//TODO add comment
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
	
	public Card randomCard(ArrayList<Card> cards) {
		int i = ThreadLocalRandom.current().nextInt(cards.size());
		//Random random = new Random();
		//int i = random.nextInt(cards.size());
		return cards.get(i);
		
	}
	
	//TODO
	public boolean rankGreater(Card card1, Card card2) {
		return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
	}
}




/*//TODO add comment
class LegalStrategy extends PlayStrategy {
	
	//TODO add comment
	@Override
	public Card execute(ArrayList<Card> playableCards, ArrayList<Card> playedCards, Suit trump, Suit lead) {
		
		ArrayList<Card> legalCards = new ArrayList<>();
		ArrayList<Card> leadCards = getCardsOfSuit(playableCards, lead);
		ArrayList<Card> trumpCards = getCardsOfSuit(playableCards, trump);
		legalCards.addAll(leadCards);
		legalCards.addAll(trumpCards);
		
		if (playedCards.size() == 0 || legalCards.size() == 0)
			return super.execute(playableCards, playedCards, trump, lead);
		else
			return super.execute(legalCards, playedCards, trump, lead);

	}
}*/

//TODO add comment
class RandomStrategy extends PlayStrategy {
	
	//TODO add comment
	@Override
	public Card execute(ArrayList<Card> playableCards, ArrayList<Card> playedCards, Suit trump, Suit lead) {
		return randomCard(playedCards);
	}
}

//TODO add comment
class LegalStrategy extends PlayStrategy {
	
	//TODO add comment
	@Override
	public Card execute(ArrayList<Card> playableCards, ArrayList<Card> playedCards, Suit trump, Suit lead) {
		
		Card card = null;
		do {
			card = randomCard(playableCards);
		}while(!isLegal(playableCards, playedCards, card, trump, lead));
		
		return card;
	}
}

//TODO add comment
class TrumpOnlyStrategy extends PlayStrategy {}

//TODO add comment
class LowestRankStrategy extends PlayStrategy{}

//TODO add comment
class HighestRankStrategy extends PlayStrategy{}