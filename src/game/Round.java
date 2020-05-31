package game;

import java.util.ArrayList;
import java.util.Optional;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import observer.Observer;
import observer.Subject;
import player.Player;
import player.PlayerFactory;
import strategy.Calculator;
import strategy.StrategyFactory;

public class Round implements Subject {
	// ------------------- Attributes --------------------
	private int winningScore;
	private Deck deck;
	private Hand trick;
	private Poker.Suit trump;
	private Poker.Suit lead;
	private Card selectedCard;

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
		for (Player player : players) {
			if (player.getId() == id)
				return player;
		}
		return null;
	}

	public boolean outOfCards() {
		for (Player player : players) {
			if (!player.getHand().isEmpty())
				return false;
		}
		return true;
	}

	// ------------------- Constructors --------------------
	// TODO add comment
	public Round(int numHumanPlayers, int numRandomNPCs, int numLegalNPCs, int numSmartNPCs, int AIThinkingTime,
			int numStartCards, int winningScore) {

		this.numStartCards = numStartCards;
		this.winningScore = winningScore;
		this.trump = Poker.randomEnum(Poker.Suit.class);
		this.deck = new Deck(Poker.Suit.values(), Poker.Rank.values(), "cover");

		players = PlayerFactory.getInstance().generatePlayers(numHumanPlayers, numRandomNPCs, numLegalNPCs,
				numSmartNPCs, AIThinkingTime);
		dealCards();
	}

	// ------------------- Methods --------------------------
	// TODO Add comment
	public void dealCards() {
		Hand[] hands = deck.dealingOut(players.size(), numStartCards); // Last element of hands is leftover cards; these
																		// are
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setHand(hands[i]);
		}
	}

	// TODO Add comment
	public Optional<Integer> playRound() {
		Integer roundWinner = null;

		//TODO change back to random
		// Choosing a random lead player on the first round
		activePlayer = players.get(0);

		// Game continues if there's no winner, re-deal if players run out of cards
		while (!outOfCards()) {
			// Shift the active player to the head of the array
			players = shiftArray(players, players.indexOf(activePlayer));
			lead = null;
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

				selectedCard = null;
				do {
					notifyObserver(); // update UI messages
					selectedCard = activePlayer.playCard(trick.getCardList(), trump, lead);
				} while (selectedCard == null);

				// TODO refactor: set leading suit to the first player's suit
				if (trick.isEmpty())
					lead = (Poker.Suit) selectedCard.getSuit();

				// Check: Following card must follow suit if possible
				boolean isLegal = Calculator.getInstance().isLegal(activePlayer.getHand().getCardList(),
						trick.getCardList(), selectedCard, trump, lead);
				if (!isLegal) {
					// Rule violation
					String violation = "Follow rule broken by player " + activePlayer.getId() + " attempting to play "
							+ selectedCard;
					System.out.println(violation);
					/*
					 * if (enforceRules) try { throw (new BrokeRuleException(violation)); } catch
					 * (BrokeRuleException e) { e.printStackTrace();
					 * System.out.println("A cheating player spoiled the game!"); System.exit(0); }
					 */
				}

				// Draw card graphics
				notifyObserver();
				selectedCard.transfer(trick, true);
			}

			// Calculate result
			Card winningCard = Calculator.getInstance().winningCard(trick.getCardList(), lead, trump);
			trickWinner = trickWinner(players, winningCard);

			if (trickWinner != null) {
				activePlayer = trickWinner;

				// Update score
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

		// Reset round if out of cards
		dealCards();
		trump = Poker.randomEnum(Poker.Suit.class);
		return Optional.empty();
	}

	// TODO add comment
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

	// ------------------- Interface Methods --------------------------
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
