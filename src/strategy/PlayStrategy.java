package strategy;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ch.aplu.jcardgame.Card;
import game.Poker;
import game.Poker.Suit;

//TODO add comment
public class PlayStrategy implements IStrategy {
	protected ArrayList<Card> candidates = new ArrayList<>();
	
	// ------------------- Interface Method --------------------
	// TODO add comment
	@Override
	public ArrayList<Card> execute(ArrayList<Card> playableCards, ArrayList<Card> playedCards, Suit trump, Suit lead) {
		// Default computer player will play a random card regardless of rules
		int i = ThreadLocalRandom.current().nextInt(playableCards.size());
			candidates.add(playableCards.get(i));
		return candidates;
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
		else if (card == null)
			return false;
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




//TODO add comment
class LegalStrategy extends PlayStrategy {
	
	//TODO add comment
	@Override
	public ArrayList<Card> execute(ArrayList<Card> playableCards, ArrayList<Card> playedCards, Suit trump, Suit lead) {
		
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
}

/*//TODO add comment
class TrumpOnlyStrategy extends PlayStrategy {
	public static PlayStrategy getInstance() {
		if (instance == null)
			instance = new TrumpOnlyStrategy();
		return instance;
	}
	//TODO add comment
	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<Card> trick, Suit trump, Suit lead) {
		
		ArrayList<Card> legalCards = getCardsOfSuit(hand, trump);
		if (legalCards.size() > 0)
			return super.execute(legalCards, trick, trump, lead);
		else
			return super.execute(hand, trick, trump, lead);

	}
}

//TODO add comment
class LowestRankStrategy extends PlayStrategy{
	public static PlayStrategy getInstance() {
		if (instance == null)
			instance = new LowestRankStrategy();
		return instance;
	}
	
	//TODO add comment
	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<Card> trick, Suit trump, Suit lead) {

		ArrayList<Card> legalCards = getCardsOfSuit(hand, lead);
		if (legalCards.size() > 0) {
			
			return lowestRank(legalCards);
		}
		else
			return super.execute(hand, trick, trump, lead);
	}
}

//TODO add comment
class HighestRankStrategy extends PlayStrategy{
	public static PlayStrategy getInstance() {
		if (instance == null)
			instance = new HighestRankStrategy();
		return instance;
	}

}*/