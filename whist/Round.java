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
	private Whist.Suit trump;
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

	public Whist.Suit getTrump() {
		return trump;
	}

	public void setTrump(Whist.Suit trump) {
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
		this.trump = Whist.randomEnum(Whist.Suit.class);

		initPlayers(playerThinkingTime);
		dealCards();
	}

	// ------------------- Methods --------------------------
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

	public void dealCards() {
		Hand[] hands = deck.dealingOut(numPlayers, numStartCards); // Last element of hands is leftover cards; these are
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setHand(hands[i]);
		}
	}

	public Optional<Integer> playRound() {
		Integer roundWinner = null;
		trick = new Hand(deck);

		// Choosing a random lead player on the first round
		activePlayer = players.get(0);


		// Game continues if there's no winner, re-deal if players run out of cards
		//boolean outOfCards = false;
		while (!outOfCards()) {
			// Shift the active player to the head of the array
			players = shiftArray(players, players.indexOf(activePlayer));
			Whist.Suit lead = null;
			trickWinner = null;

			// Each player plays a card
			for (Player player : players) {
				// Skips player if no card in hand
				if (player.getHand().isEmpty())
					continue;

				//switch active player
				activePlayer = player;
				
				// TODO refactor: clear last round's selectedCard
				activePlayer.setSelectedCard(null);

				// TODO Will cause infinite loop when player plays no card
				Card playedCard = null;
				while (null == playedCard) {
					notifyObserver();
					playedCard = activePlayer.playCard();
				}

				// TODO refactor: lead selection
				if (players.indexOf(activePlayer) == 0)
					lead = (Whist.Suit) activePlayer.getSelectedCard().getSuit();

				// Draw card graphics
				notifyObserver();
				activePlayer.getSelectedCard().transfer(trick, true);
			}

			// Calculate result
			trickWinner = decideWinner(players, lead, trump);
			if (trickWinner != null) {
				activePlayer = trickWinner;
				trickWinner.setScore(trickWinner.getScore() + 1);
				trick.removeAll(true);
				notifyObserver();

				// End game if winner is born
				if (trickWinner.getScore() == winningScore) {
					roundWinner = trickWinner.getId();
					return Optional.of(roundWinner);
				}
			}
		}
		dealCards();
		return Optional.empty();
	}

	// TODO add javadoc comment
	private Player decideWinner(ArrayList<Player> players, Whist.Suit lead, Whist.Suit trump) {
		Player winner = players.get(0);
		Card winningCard = players.get(0).getSelectedCard();

		for (int i = 1; i < players.size(); i++) {
			Player player = players.get(i);

			// Skips player if no card in hand
			if (player.getHand().isEmpty())
				continue;

			if ( // beat current winner with higher card
			(player.getSelectedCard().getSuit() == winningCard.getSuit()
					&& rankGreater(player.getSelectedCard(), winningCard)) ||
			// trumped when non-trump was winning
					(player.getSelectedCard().getSuit() == trump && winningCard.getSuit() != trump)) {
				winner = player;
			}
		}
		System.out.println("NEW WINNER: Player " + winner.getId());
		return winner;
	}

	// TODO add javadoc comment
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
