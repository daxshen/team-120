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
	private Player nextPlayer;
	private Player winner;
	private ArrayList<Player> players = new ArrayList<>();
	private ArrayList<Observer> observers = new ArrayList<>();

	// ------------------- Getters & Setters ---------------
	public Player getWinner() {
		return winner;
	}

	public void setWinner(Player winner) {
		this.winner = winner;
	}

	public Player getNextPlayer() {
		return nextPlayer;
	}

	public void setNextPlayer(Player nextPlayer) {
		this.nextPlayer = nextPlayer;
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

	// ------------------- Constructors --------------------
	public Round(Deck deck, int numPlayers, int playerThinkingTime, int numStartCards, int winningScore) {

		this.deck = deck;
		this.numPlayers = numPlayers;
		this.numStartCards = numStartCards;
		this.winningScore = winningScore;
		this.trump = Whist.randomEnum(Whist.Suit.class);

		initPlayers(playerThinkingTime);
	}

	// ------------------- Methods --------------------------
	private void initPlayers(int playerThinkingTime) {
		//Hand[] hands = deck.dealingOut(numPlayers, numStartCards); // Last element of hands is leftover cards; these are
																	// ignored
		for (int i = 0; i < numPlayers; i++) {
			// TODO specify player type according to id
			Player player = null;
			if (i == 0) player = new HumanPlayer(i, null, playerThinkingTime);else
				player = new ComputerPlayer(i, null, playerThinkingTime);
			players.add(player);
		}
	}
	
	private void dealCards() {
		Hand[] hands = deck.dealingOut(numPlayers, numStartCards); // Last element of hands is leftover cards; these are
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setHand(hands[i]);
		}
	}

	public Optional<Integer> playRound() {
		dealCards();
		Integer gameWinner = null;
		// Choosing a random lead player on the first round
		// Player nextPlayer = players.get(random.nextInt(nbPlayers));
		nextPlayer = players.get(0);

		// Keep playing the round till there is a winner
		//while (gameWinner == null) {
			winner = null;
			trick = new Hand(deck);

			// Shift the active player to the head of the array
			players = shiftArray(players, players.indexOf(nextPlayer));
			Whist.Suit lead = null;

			// Each player plays a card
			for (Player player : players) {
				nextPlayer = player;
				// TODO refactor: clear last round's selectedCard
				nextPlayer.setSelectedCard(null);

				// TODO Will cause infinite loop when player plays no card
				Card playedCard = null;
				while (null == playedCard) {
					notifyObserver();
					playedCard = nextPlayer.playCard();
				}

				// TODO refactor: lead selection
				if (players.indexOf(nextPlayer) == 0)
					lead = (Whist.Suit) nextPlayer.getSelectedCard().getSuit();

				// Draw card graphics
				notifyObserver();
				nextPlayer.getSelectedCard().transfer(trick, true);
			}

			// Calculate result
			winner = decideWinner(players, lead, trump);
			if (winner != null) {
				nextPlayer = winner;
				winner.setScore(winner.getScore() + 1);
				notifyObserver();

				// End game if winner is born
				if (winner.getScore() == winningScore) {
					gameWinner = winner.getId();
					return Optional.of(gameWinner);
				}
			}
		//}

		return Optional.empty();
	}

	// TODO add javadoc comment
	private Player decideWinner(ArrayList<Player> players, Whist.Suit lead, Whist.Suit trump) {
		Player winner = players.get(0);
		Card winningCard = players.get(0).getSelectedCard();

		for (int i = 1; i < players.size(); i++) {
			Player player = players.get(i);
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
