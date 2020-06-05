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
	
	private void updateLastTrick(ArrayList<Card> lastTrick) {
	
		for(Card card: lastTrick) {
			if(!mapPlayedCards.containsKey((Suit) card.getSuit())) {
				ArrayList<Rank> ranks = new ArrayList<>();
				mapPlayedCards.put((Suit) card.getSuit(), ranks);
				
			}
			mapPlayedCards.get(card.getSuit()).add((Rank) card.getRank());
		}
	}
	private void refreshAtRoundEnd() {
		 mapPlayedCards = new HashMap<Suit, ArrayList<Rank>>();
	}
	private String TrickPostion(ArrayList<Card> trick) {
		int trickTurnsTaken = trick.size();
		
		if (trickTurnsTaken == 0)
			return "LEAD";
		else if (trickTurnsTaken == 1 || trickTurnsTaken == 2)
			return "CENTRAL";
		else
			return "LAST";
		
	}
	private boolean initialisePlayableCards(ArrayList<Card>  smartCards, ArrayList<Card> hand, Suit lead, Suit trump) {
		for (Card handCard : hand) {
			if (handCard.getSuit() == lead) //trump cards might not be allowed when 
				smartCards.add(handCard);
			
		}
		if(smartCards.isEmpty()) {
			smartCards = hand;
			return true;
		}
		
		return false;
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
			System.out.println(maxCards.get(suit));
		}
		
		//filtering based on card-counting
		if(!mapPlayedCards.isEmpty()) {
				countGuess(smartCards);
		}
	}
	
	private void countGuess(ArrayList<Card>  smartCards) {
		for(Card card: smartCards) {
			ArrayList<Rank> playedSuitCards = mapPlayedCards.get((Suit) card.getSuit());
			for(Rank playedCard: playedSuitCards) {
				if(playedCard.compareTo((Rank) card.getRank()) > 0) {
					playedSuitCards.remove(playedCard);
				}
			}
			
			for( Rank rank: Rank.values()) {
				if(rank.compareTo((Rank) card.getRank()) > 0 && !playedSuitCards.contains(rank)) //ACE never removed
					smartCards.remove(card);
					
			}
			
		}
	}
	
	private void leadVoidWinning(ArrayList<Card>  smartCards, Suit trump) {
		for(Card card: smartCards) {
			if( card.getSuit()!= trump) {
				smartCards.remove(card);
			}
		}
	}
	
	private void winningPlay(ArrayList<Card>  smartCards, ArrayList<Card> currentTrick, Suit trump, Suit lead) {
		
		Card maxTrickCard = currentTrick.get(0);
		for (Card card: currentTrick) {
			if((card.getSuit() == maxTrickCard.getSuit() && maxTrickCard.getRankId() < card.getRankId()) || card.getSuit() == trump) {
				maxTrickCard = card;
			}
		}
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
			refreshAtRoundEnd();
		
		if(!playedCards.isEmpty()) {//going first in first round
			
			System.out.println("COLD-START OFF");
			ArrayList <Card > trick = playedCards.get(playedCards.size() - 1 );
			
			if(initialisePlayableCards(smartCards, playableCards, lead, trump))
				leadVoidWinning(smartCards, trump);
			System.out.println("BREAK 1:" + smartCards.toString());

			if(playedCards.size()==2)
				updateLastTrick(playedCards.get(playedCards.size() - 2 ));
			
			switch(TrickPostion(trick)) {
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
			
		}
		else {
			initialisePlayableCards(smartCards, playableCards, lead, trump);
			guessWinningPlay(smartCards);
		}
		System.out.println("BREAK 2: " + smartCards.toString());
		
		if(smartCards.isEmpty())
			return null;
		else
		   return smartCards.get(0);
	}
	

}

