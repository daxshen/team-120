package strategy;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ch.aplu.jcardgame.Card;
import game.Poker;
import game.Poker.Suit;

//TODO add comment
public class PlayStrategy implements IStrategy {
	// ------------------- Interface Method --------------------
	// TODO add comment
	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<Card> trick, Suit trump, Suit lead) {
		// Default computer player will play a random card regardless of rules
		int i = ThreadLocalRandom.current().nextInt(hand.size());
		return hand.get(i);
	}
}




//TODO add comment
class LegalStrategy extends PlayStrategy {
	
	//TODO add comment
	@Override
	public Card execute(ArrayList<Card> hand, ArrayList<Card> trick, Suit trump, Suit lead) {
		
		ArrayList<Card> legalCards = new ArrayList<>();
		ArrayList<Card> leadCards = Calculator.getInstance().getCardsOfSuit(hand, lead);
		ArrayList<Card> trumpCards = Calculator.getInstance().getCardsOfSuit(hand, trump);
		legalCards.addAll(leadCards);
		legalCards.addAll(trumpCards);
		
		if (trick.size() == 0 || legalCards.size() == 0)
			return super.execute(hand, trick, trump, lead);
		else
			return super.execute(legalCards, trick, trump, lead);

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