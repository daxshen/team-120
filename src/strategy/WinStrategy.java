package strategy;

import java.util.ArrayList; 
import java.util.HashMap;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;
import game.Poker.Rank;

public class WinStrategy extends Strategy{
	
	private HashMap<Suit, ArrayList<Rank>> mapPlayedCards = new HashMap<>();

	// ------------------- Methods -------------------------
	public WinStrategy() {
		 mapPlayedCards = new HashMap<Suit, ArrayList<Rank>>();
		}
	
	private void updateLastTrick(ArrayList<ArrayList<Card>>  tricks) {
		
		for(ArrayList<Card> trick: tricks) {
			System.out.println("Trick Counter:" + trick.toString());
			if(trick.size() == 4) {
				for(Card card: trick) {
					if(!mapPlayedCards.containsKey((Suit) card.getSuit())) {
						ArrayList<Rank> ranks = new ArrayList<>();
						mapPlayedCards.put((Suit) card.getSuit(), ranks);
						
					}
						mapPlayedCards.get(card.getSuit()).add((Rank) card.getRank());
				}
				System.out.println("Updated Card Counter:" + mapPlayedCards.toString());
			}
		}
	}
	
	private void refreshAtRoundEnd() {
		 mapPlayedCards = new HashMap<Suit, ArrayList<Rank>>();
	}
	
	private String TrickPosition(ArrayList<Card> trick) {
		int trickTurnsTaken = trick.size();
		
		if (trickTurnsTaken == 0)
			return "LEAD";
		else if (trickTurnsTaken == 1 || trickTurnsTaken == 2)
			return "CENTRAL";
		else
			return "LAST";
		
	}
	
	private boolean initialisePlayableCards(ArrayList<Card>  smartCards, ArrayList<Card> hand, Suit lead, Suit trump, String trickPosition) {

	
	if(trickPosition == "LEAD") {
		smartCards.addAll(hand);
		return true;
	}
	
	smartCards.addAll(getCardsOfSuit(hand,lead));
	if(smartCards.isEmpty()) {
		smartCards.addAll(getCardsOfSuit(hand,trump));
	}
	if(smartCards.isEmpty()) {
		return false;
	}
	else
		return true;
	
}	
	
	private void guessWinningPlay(ArrayList<Card>  smartCards){
		HashMap<Suit,Card> maxCards = new HashMap<>();
		for (Card card: smartCards) {
			if(!maxCards.containsKey((Suit) card.getSuit())) {
				maxCards.put((Suit) card.getSuit(), card);
			}
			else if(maxCards.get((Suit) card.getSuit()).getRankId() > card.getRankId() ) {
				maxCards.put((Suit) card.getSuit(), card);
			}
		}
		smartCards = new ArrayList<>();
		
		for(Suit suit: maxCards.keySet()) {
			smartCards.add(maxCards.get(suit));
		}
		
		//filtering based on card-counting
		if(!mapPlayedCards.isEmpty()) {
			System.out.print("Count guess: ");
			countGuess(smartCards);
		}
	}
	
	private void countGuess(ArrayList<Card>  smartCards) {
		for(Card card: smartCards) {
			ArrayList<Rank> playedSuitCards = new ArrayList<>();
			playedSuitCards.addAll(mapPlayedCards.get((Suit) card.getSuit()));
			for(Rank playedCard: playedSuitCards) {
				if(playedCard.compareTo((Rank) card.getRank()) > 0) {
					playedSuitCards.remove(playedCard);
				}
			}
			System.out.print(playedSuitCards.toString());
			for( Rank rank: Rank.values()) {
				if(rank.compareTo((Rank) card.getRank()) > 0 && !playedSuitCards.contains(rank)) //ACE never removed
					System.out.println("smaller card: " + card);
					smartCards.remove(card);
					
			}
			
		}
	}
	
	private void winningPlay(ArrayList<Card>  smartCards, ArrayList<Card> currentTrick, Suit trump, Suit lead) {
		
		Card maxTrickCard = winningCard(currentTrick, trump, lead);
		
		ArrayList<Card> winningCards = new ArrayList<Card>();
		winningCards.addAll(smartCards);
		for( Card card: smartCards) {
			if(maxTrickCard.getSuit() == trump && card.getSuit() == lead) {
				winningCards.clear();
				break;
			}
			else if(maxTrickCard.getSuit() == lead && card.getSuit() == trump) {
				
			}
			else if(maxTrickCard.getRankId() < card.getRankId()) {
				winningCards.remove(card);
			}
		}

		smartCards.clear();
		smartCards.addAll(winningCards);
			
	}

	@Override 
	public Card execute(ArrayList<Card> playableCards, ArrayList<ArrayList<Card>> playedCards, Suit trump, Suit lead) {
		
		ArrayList<Card>  smartCards = new ArrayList<>();
		
		if(playedCards.isEmpty() && !mapPlayedCards.isEmpty())
			System.out.println("ROUND ENDED");
			refreshAtRoundEnd();
		
		
		ArrayList <Card > trick = new ArrayList<>();
		trick.addAll(playedCards.get(playedCards.size() - 1 ));
		String trickPosition = TrickPosition(trick);
		
		
		if(!initialisePlayableCards(smartCards, playableCards, lead, trump, trickPosition)) {
			System.out.println("LOSING HAND"+ playableCards.toString());
			return null;
		}
		
		if(playedCards.size() > 1) {
			updateLastTrick(playedCards);
		}
		
		System.out.println("Break 1:" + smartCards.toString());
		switch(trickPosition) {
		case "LEAD":{ guessWinningPlay(smartCards);
						break;
						}
		case "CENTRAL":{winningPlay(smartCards, trick, trump, lead);
					   guessWinningPlay(smartCards);
					   break;
					   }
		case "LAST": {winningPlay(smartCards, trick, trump, lead);
					  break;
					 }
		default: //throw error (remove after testing)
			
		}
		
		System.out.println("Break 2:" + smartCards.toString());
		
		if(smartCards.isEmpty())
			return null;
		else
		   return highestRank(smartCards);
	}
	

}

