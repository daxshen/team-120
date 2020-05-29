
import java.util.ArrayList;
import java.util.Optional;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import observer.Observer;
import observer.Subject;
import player.ComputerPlayer;
import player.HumanPlayer;
import player.Player;

public class Round implements Subject {
	// ------------------- Attributes --------------------
	private int winningScore;
	private Player roundWinner;
	private Deck deck;
	private Hand trick;
	private Poker.Suit trump;
	private int numPlayers;
	private int numStartCards;
	private Player activePlayer;
	private Player trickWinner;
	private ArrayList<Player> players = new ArrayList<>();
	private ArrayList<Observer> observers = new ArrayList<>();

	// ------------------- Getters & Setters ---------------
	public Player getTrickWinner() {
		return trickWinner;
	}

	public void setTrickWinner(Player trickWinner) {
		this.trickWinner = trickWinner;
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public void setActivePlayer(Player nextPlayer) {
		this.activePlayer = nextPlayer;
	}

	public Poker.Suit getTrump() {
		return trump;
	}

	public void setTrump(Poker.Suit trump) {
		this.trump = trump;
	}

	public int getWinningScore() {
		return winningScore;
	}

	public void setWinningScore(int winningScore) {
		this.winningScore = winningScore;
	}

	public Player getRoundWinner() {
		return roundWinner;
	}

	public void setRoundWinner(Player winner) {
		this.roundWinner = winner;
	}

	public Deck getDeck() {
		return deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	public Hand getTrick() {
		return trick;
	}

	public void setTrick(Hand trick) {
		this.trick = trick;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public int getPlayerId(int index) {
		return players.get(index).getId();
	}

	public int getPlayerScore(int index) {
		return players.get(index).getScore();
	}

	public Hand getPlayerHand(int index) {
		return players.get(index).getHand();
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public Player getPlayerById(int id) {
		for (Player player: players) {
			if(player.getId() == id)
				return player;
		}
		return null;
	}
	
	public boolean outOfCards() {
		for (Player player: players) {
			if (!player.getHand().isEmpty())
				return false;
		}
		return true;
	}

	// ------------------- Constructors --------------------
	public Round(Deck deck, int numPlayers, int playerThinkingTime, int numStartCards, int winningScore) {

		this.deck = deck;
		this.numPlayers = numPlayers;
		this.numStartCards = numStartCards;
		this.winningScore = winningScore;
		this.trump = Poker.randomEnum(Poker.Suit.class);

		initPlayers(playerThinkingTime);
		dealCards();
	}

	// ------------------- Methods --------------------------
	//TODO Add comment
	private void initPlayers(int playerThinkingTime) {
		// Hand[] hands = deck.dealingOut(numPlayers, numStartCards); // Last element of
		// hands is leftover cards; these are
		// ignored
		for (int i = 0; i < numPlayers; i++) {
			// TODO specify player type according to id
			Player player = null;
			if (i == 0)player = new HumanPlayer(i, null, playerThinkingTime);else
				player = new ComputerPlayer(i, null, playerThinkingTime);
			players.add(player);
		}
	}

	//TODO Add comment
	public void dealCards() {
		Hand[] hands = deck.dealingOut(numPlayers, numStartCards); // Last element of hands is leftover cards; these are
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setHand(hands[i]);
		}
	}

	
	//TODO Add comment
	public Optional<Integer> playRound() {
		Integer roundWinner = null;

		// Choosing a random lead player on the first round
		activePlayer = players.get(0);


		// Game continues if there's no winner, re-deal if players run out of cards
		//boolean outOfCards = false;
		while (!outOfCards()) {
			// Shift the active player to the head of the array
			players = shiftArray(players, players.indexOf(activePlayer));
			Poker.Suit lead = null;
			trickWinner = null;
			trick = new Hand(deck);

			// Each player plays a card
			System.out.println(" Trump: " + trump.toString());
			for (Player player : players) {
				// Skips player if no card in hand
				if (player.getHand().isEmpty())
					continue;

				activePlayer = player;
				
				// TODO (not sure if needed): clear last round's selectedCard
				activePlayer.setSelectedCard(null);

				Card playedCard = null;
				do {
					notifyObserver(); //update UI messages
					playedCard = activePlayer.playCard();
				} while (playedCard == null);

				// TODO refactor: set leading suit to the first player's suit
				if (players.indexOf(activePlayer) == 0)
					lead = (Poker.Suit) activePlayer.getSelectedCard().getSuit();

				// Draw card graphics
				notifyObserver();
				activePlayer.getSelectedCard().transfer(trick, true);
				System.out.println(" Player " + activePlayer.getId() + " : suit = " 
				+ activePlayer.getSelectedCard().getSuit() 
				+ ", rank = " 
				+ activePlayer.getSelectedCard().getRankId());
			}

			// Calculate result
			trickWinner = trickWinner(players, winningCard(trick.getCardList(), lead, trump));
			if (trickWinner != null) {
				activePlayer = trickWinner;
				
				//Update score
				System.out.println(" Winner: Player " + trickWinner.getId());
				System.out.println("-----------------------------------------");
				trickWinner.setScore(trickWinner.getScore() + 1);
				notifyObserver();

				// End game if player reached winning score
				if (trickWinner.getScore() == winningScore) {
					roundWinner = trickWinner.getId();
					return Optional.of(roundWinner);
				}
			}
			trick.removeAll(true);
		}
		
		//Reset round if out of cards
		dealCards();
		trump = Poker.randomEnum(Poker.Suit.class);
		return Optional.empty();
	}

	// TODO add comment
	private Card winningCard(ArrayList<Card> cards, Poker.Suit lead, Poker.Suit trump) {
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
	
	//TODO add comment
	private Player trickWinner(ArrayList<Player> players, Card winningCard) {
		Player trickWinner = null;
		for (Player player : players) {
			if (player.getSelectedCard() == winningCard)
				trickWinner = player;
		}
		return trickWinner;
	}
	
	// ------------------- Utility Methods --------------------------
	// TODO add comment
	// Shift all elements to re-order array
	private <T> ArrayList<T> shiftArray(ArrayList<T> list, int index) {

		// Do nothing if element is already the first
		if (index == 0)
			return list;

		ArrayList<T> newList = (ArrayList<T>) list.clone();

		// The number of steps needed for the element to be shifted to the head of the
		// array
		int shift = list.size() - index;

		// Shift all elements
		for (int oldIndex = 0; oldIndex < list.size(); oldIndex++) {
			int newIndex = oldIndex + shift;

			// Handles element "overflow" when index exceeds list size
			if (newIndex >= list.size())
				newIndex -= list.size();

			// Replace all elements in shifted order
			newList.set(newIndex, list.get(oldIndex));
		}
		return newList;
	}

	public boolean rankGreater(Card card1, Card card2) {
		return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
	}

	// Observer pattern
	@Override
	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	@Override
	public void notifyObserver() {
		for (Observer observer : observers) {
			observer.update();
		}
	}
}
