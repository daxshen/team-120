package strategy;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ch.aplu.jcardgame.Card;
import game.Poker;
import game.Poker.Suit;
import player.Player;

/**
 * This class handles the logic of {@linkplain Card} selection during a
 * {@linkplain Player}'s turn
 */
public abstract class Strategy {

	// ------------------ Utility Methods -------------------
	/**
	 * Returns all cards of a certain suit
	 * 
	 * @param cards the {@linkplain ArrayList} of {@linkplain Card}s to search from
	 * @param suit  the target suit
	 * @return an {@linkplain ArrayList} of {@linkplain Card}s that match the suit
	 */
	public ArrayList<Card> getCardsOfSuit(ArrayList<Card> cards, Suit suit) {
		ArrayList<Card> cardsofSuit = new ArrayList<>();
		for (Card card : cards) {
			if (card.getSuit() == suit)
				cardsofSuit.add(card);
		}
		return cardsofSuit;
	}

	/**
	 * Determines whether a {@linkplain Card} is legal to play
	 * 
	 * @param hand  all {@linkplain Card}s in hand
	 * @param trick all other {@linkplain Cards} played in the trick so far
	 * @param card  the {@linkplain Card} in question
	 * @param trump the trump suit of the round
	 * @param lead  the leading {@linkplain Card}'s suit of the trick
	 * @return true - the {@linkplain Card} is legal to play
	 *         <p>
	 *         false - is illegal to play
	 *         </p>
	 */
	public boolean isLegal(ArrayList<Card> hand, ArrayList<Card> trick, Card card, Suit trump, Suit lead) { // trick is not used
		// Can play any card when player is the lead or when no suitable card
		if (lead == null)
			return true;

		// Find the list of allowed-to-play cards
		ArrayList<Card> legalCards = new ArrayList<>();
		for (Card handCard : hand) {
			if (handCard.getSuit() == lead)
				legalCards.add(handCard);
		}

		// If no legal cards, play any card
		if (legalCards.size() == 0)
			return true;

		// If there are legal cards when the selected card is not among them, return
		// false
		if (!legalCards.contains(card))
			return false;
		// If the card is among the legal cards, return true, meaning it's legal
		else
			return true;
	}

	/** returns the {@linkplain Card} with the lowest rank*/
	public Card lowestRank(ArrayList<Card> cards) {
		Card lowest = null;
		if (cards.size() > 0) {
			lowest = cards.get(0);
			for (int i = 1; i < cards.size(); i++) {
				if (!rankGreater(cards.get(i), lowest))
					lowest = cards.get(i);
			}
		}
		return lowest;
	}

	/** returns the {@linkplain Card} with the highest rank*/
	public Card highestRank(ArrayList<Card> cards) {
		Card highest = null;
		if (cards.size() > 0) {
			highest = cards.get(0);
			for (int i = 1; i < cards.size(); i++) {
				if (rankGreater(cards.get(i), highest))
					highest = cards.get(i);
			}
		}
		return highest;
	}

	/**
	 * Determines the {@linkplain Card} that is winning (regardless of whether the
	 * trick is still ongoing or not)
	 * 
	 * @param cards all {@linkplain Cards} played in the trick so far
	 * @param trump the trump suit of the round
	 * @param lead  the leading {@linkplain Card}'s suit of the trick
	 * @return the winning {@linkplain Card}
	 */
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

			if ((sameSuit && rankGreater) || (isTrump && winningCardNotTrump)) {
				winningCard = card;
			}
		}
		return winningCard;
	}

	/**
	 * Returns a random {@linkplain Card}
	 * 
	 * @param cards the {@linkplain ArrayList} of {@linkplain Card}s to select from
	 */
	public Card randomCard(ArrayList<Card> cards) {
		int i = ThreadLocalRandom.current().nextInt(cards.size());
		// Random random = new Random();
		// int i = random.nextInt(cards.size());
		return cards.get(i);

	}

	/**
	 * Determines whether a {@linkplain Card}1's rank is greater than 2's
	 * 
	 * @return true if {@linkplain Card}1 ranks greater than 2
	 */
	public boolean rankGreater(Card card1, Card card2) {
		return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
	}

	/**
	 * The main logic of selecting a {@linkplain Card} to play
	 * 
	 * @param hand           all {@linkplain Card}s in hand
	 * @param previousTricks cards from all tricks played so far, including the
	 *                       previous tricks. The last element is all the cards from
	 *                       the current trick
	 * @param card           the {@linkplain Card} in question
	 * @param trump          the trump suit of the round
	 * @param lead           the leading {@linkplain Card}'s suit of the current
	 *                       trick
	 */
	abstract public Card execute(ArrayList<Card> hand, ArrayList<ArrayList<Card>> previousTricks, Suit trump,
			Suit lead);
}

/** This {@linkplain Strategy} plays a random {@linkplain Card} */
class RandomStrategy extends Strategy {

	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<ArrayList<Card>> previousTricks, Suit trump, Suit lead) {
		return randomCard(hand);
	}
}

/** This {@linkplain Strategy} plays a random, legal {@linkplain Card} */
class LegalStrategy extends Strategy {

	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<ArrayList<Card>> previousTricks, Suit trump, Suit lead) {

		Card card = null;
		do {
			// Keep selecting a random card until it's a legal card
			card = randomCard(hand);
		} while (!isLegal(hand, previousTricks.get(previousTricks.size() - 1), card, trump, lead));

		return card;
	}
}

//TODO Create some example strategies
//class TrumpOnlyStrategy extends PlayStrategy {}
 
//class LowestRankStrategy extends PlayStrategy{}
  
//class HighestRankStrategy extends PlayStrategy{}
 