package strategy;

import java.util.ArrayList; 
import java.util.HashMap;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;
import game.Poker.Rank;

/** Class consisting {@linkplain Strategy} for smart NPC to win, 
 *  by eliminating non-winning {@linkplain ArrayList} of 
 *  {@linkplain Card} from pseudo-Hand
 *  */
public class WinStrategy extends Strategy{
	// Maps all Ranks (value) to its Suit (key) which are already played
	private HashMap<Suit, ArrayList<Rank>> mapPlayedCards = new HashMap<>();
	
	//List of all winning Cards
	private ArrayList<Card>  smartCards = new ArrayList<>();
	// ------------------- Methods -------------------------
	
	/** Constructor initializing {@value mapPlayedCards}
	 * */
	public WinStrategy() {
		 mapPlayedCards = new HashMap<Suit, ArrayList<Rank>>();
		}
	
	/** Method Mapping {@linkplain Rank} to {@linkplain  Suit} of Played cards in  mapPlayedCards
	 * @param tricks	an {@linkplain ArrayList} of {@linkplain ArrayList} of {@linkplain Card} 
	 * 					storing all tricks, including the current one in play
	 * */
	private void updateLastTrick(ArrayList<ArrayList<Card>>  tricks) {
		
		//iterating over all previous tricks (excluding current trick in play)
		for(ArrayList<Card> trick: tricks) {
			if(trick.size() == 4) {
				
				//Iterating over all cards in trick 
				for(Card card: trick) {
					
					//adding unknown keys(Suit) to mapPlayedCards
					if(!mapPlayedCards.containsKey((Suit) card.getSuit())) {
						ArrayList<Rank> ranks = new ArrayList<>();
						mapPlayedCards.put((Suit) card.getSuit(), ranks);
						
					}
					//adding ranks for specific suit to mapPlayedCards
						mapPlayedCards.get(card.getSuit()).add((Rank) card.getRank());
						
				}
			}
		}
	}
	/** This method clears and sets new {@linkplain HashMap} mapPlayedCards at round end
	 * */
	private void refreshAtRoundEnd() {
		mapPlayedCards.clear();
		mapPlayedCards = new HashMap<Suit, ArrayList<Rank>>();
	}
	
	/** Determines the position of the player
	 * @param trick	the current {@linkplain ArrayList} of {@linkplain Card} being played 
	 * @return a {@linkplain String} of player position
	 * */

	private String TrickPosition(ArrayList<Card> trick) {
		int trickTurnsTaken = trick.size();
		
		// Determining relative position by number of cards played in trick
		if (trickTurnsTaken == 0)
			return "LEAD";
		else if (trickTurnsTaken == 1 || trickTurnsTaken == 2)
			return "CENTRAL";
		else
			return "LAST";
		
	}
	
	/** Initializes the playable cards in hand by updating {@linkplain ArrayList} of 
	 *  {@linkplain Card} smartCards
	 * 
	 * @param hand	the {@linkplain ArrayList} of {@linkplain Card} which are the playable cards
	 * @param lead	the lead {@linkplain Suit}
	 * @param trump	the trump {@linkplain Suit}
	 * @return whether player has winning cards ( trump or lead {@linkplain Suit}
	 * */

	private boolean initialisePlayableCards(ArrayList<Card> hand, Suit lead, Suit trump, String trickPosition) {

	//All cards are playable at lead position
	if(trickPosition == "LEAD") {
		smartCards.addAll(hand);
		return true;
	}
	// Adding Lead Suit Cards
	smartCards.addAll(getCardsOfSuit(hand,lead));
	
	//If no Lead suit Cards adding trump Suit cards
	if(smartCards.isEmpty()) {
		smartCards.addAll(getCardsOfSuit(hand,trump));
	}
	
	//If no lead and trump cards return false (not a winning hand)
	if(smartCards.isEmpty()) {
		return false;
	}
	else
		return true;
	
}	
	/** Finds the Highest {@linkplain Rank} of {@linkplain Card} in every {@linkplain Suit}  and updates 
	 * 	{@linkplain ArrayList} of {@linkplain Card} smartCards. 
	 * */
	private void guessWinningPlay(){
		HashMap<Suit,Card> maxCards = new HashMap<>();
		
		//Iterating over all current winning cards
		for (Card card: smartCards) {
			
			//Mapping Highest Ranked Card to its Suit 
			if(!maxCards.containsKey((Suit) card.getSuit())) {
				maxCards.put((Suit) card.getSuit(), card);
			}
			else if(maxCards.get((Suit) card.getSuit()).getRankId() > card.getRankId() ) {
				maxCards.put((Suit) card.getSuit(), card);
			}
		}
		
		//Updating smartCards ( winning cards )
		smartCards.clear();
		
		for(Suit suit: maxCards.keySet()) {
			smartCards.add(maxCards.get(suit));
		}
		//If history of play7ed cards exist counting cards using count guess function
		if(!mapPlayedCards.isEmpty()) {
			countGuess();
		}
	}
	/** Checks whether updated {@linkplain ArrayList} of {@linkplain Card} smartCards are the Highest 
	 * 	{@linkplain Rank}ed {@linkplain Card}'s to be played in the current trick, by checking whether
	 * 	Higher {@linkplain Rank}ed {@linkplain Card}'s of the same suit are already played.
	 * */
	private void countGuess() {
		
		/* New arrayList of winning cards to avoid Concurrent Modification exception 
		 * on iterating over smartCards 
		 * */
		ArrayList<Card> nonElimnatedSmartCards = new ArrayList<>();
		nonElimnatedSmartCards.addAll(smartCards);
		
		//Iterating over smartCards before countGuess elimination
		for(Card card: nonElimnatedSmartCards) {
			
			//Condition to check Suit cards are played before
			if(mapPlayedCards.get((Suit) card.getSuit())!= null) {
				
				/* ArrayList of Ranks storing played Ranks of current iteration card Suit from HashMap 
				 * mapPlayedCards */
				ArrayList<Rank> playedSuitCards = new ArrayList<>();
				playedSuitCards.addAll(mapPlayedCards.get((Suit) card.getSuit()));
				
				//Iterating over Ranks of specific suit
				for(Rank playedCard: mapPlayedCards.get((Suit) card.getSuit())) {
					
					//Stores higher played Ranks than the card of specific suit in current iteration
					if(playedCard.compareTo((Rank) card.getRank()) > 0) {
						playedSuitCards.remove(playedCard);
					}
				}
				//Iterates over all ranks in a suit
				for( Rank rank: Rank.values()) {
					
					/* Checks whether a higher card rank than the current card rank exists and is already 
					 * played or not played. If not played, that card is removed (because waste of 
					 * high value card; ACE never removed) */
					if(rank.compareTo((Rank) card.getRank()) < 0 && !playedSuitCards.contains(rank))
						smartCards.remove(card);
						
				}
			}
		}
	}
	/** Method to decide @{@linkplain ArrayList} of {@linkplain Card}'s winning the current 
	 *  trick/partial trick
	 *  
	 *  @param currentTrick	the {@linkplain ArrayList} of {@linkplain Card} which are the current 
	 *  					trick/partial trick cards
	 *  
	 *  @param lead			the lead {@linkplain Suit}
	 *  @param trump		the trump {@linkplain Suit}
	 * */
	private void winningPlay( ArrayList<Card> currentTrick, Suit trump, Suit lead) {
		
		//winning card played in current trick 
		Card maxTrickCard = winningCard(currentTrick, trump, lead);
		
		/* New arrayList of winning cards to avoid Concurrent Modification exception 
		 * on iterating over smartCards 
		 * */
		ArrayList<Card> winningCards = new ArrayList<Card>();
		winningCards.addAll(smartCards);
		
		//Iterating over all possible winning cards 
		for( Card card: winningCards) {
			
			//If maxTrickCard trump card, remove lead cards to win
			if(maxTrickCard.getSuit() == trump && card.getSuit() == lead) {
				smartCards.remove(card);
			}
			//If maxTrickCard is lead card, don't remove trump cards from winning cards
			else if(maxTrickCard.getSuit() == lead && card.getSuit() == trump) {
				
			}
			//If maxTrickCard and card are both trump or lead, remove card if it 
			//has a smaller rank
			else if(maxTrickCard.getRankId() < card.getRankId()) {
				smartCards.remove(card);
			}
		}
		//empty winning cards
		winningCards.clear();
			
	}

	@Override 
	public Card execute(ArrayList<Card> playableCards, ArrayList<ArrayList<Card>> playedCards, Suit trump, Suit lead) {
		
		
		//Check whether round refreshed and refresh HashMap mapPlayedCards
		if(playedCards.isEmpty() && !mapPlayedCards.isEmpty())
			refreshAtRoundEnd();
		
		// Determine the trick and trick position
		ArrayList <Card > trick = new ArrayList<>();
		trick.addAll(playedCards.get(playedCards.size() - 1 ));
		String trickPosition = TrickPosition(trick);
		
		//Initialize playablCards; if no winning cards exists return null
		if(!initialisePlayableCards(playableCards, lead, trump, trickPosition)) {
			return null;
		}
		//If tricks have been played, mapped in mapPlayedCards
		if(playedCards.size() > 0) {
			updateLastTrick(playedCards);
		}
		
		//Main logic depending on position to elimnating non-winning cards from SmartCards
		switch(trickPosition) {
		case "LEAD":	{ 	
						//Lead position can only guess the best winning card
						guessWinningPlay();
						break;
						}
		case "CENTRAL":	{
						//Central positions need to win partial trick and then guess whether winning cards
						//can actually win
						winningPlay( trick, trump, lead);
						guessWinningPlay();
						break;
					   	}
		case "LAST": 	{
						//Last Positions always needs to win the trick if possible
						winningPlay(trick, trump, lead);
						break;
					 	}
			
		}
		
		// If winning Cards exist returns the highest ranked card, else returns null
		if(smartCards.isEmpty())
			return null;
		else
		   return highestRank(smartCards);
	}
	

}

