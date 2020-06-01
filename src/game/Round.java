package game;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import observer.Observer;
import observer.Subject;
import player.Player;
import player.PlayerFactory;
import strategy.StrategyFactory;

/**
 * This class handles the gameplay logic of an entire round of Whist game, which consists of multiple tricks
 * */
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
	
	/** A list of observers that monitors the changes within{@linkplain Round} */
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
	/** 
	 * Initialises the {@linkplain Player}s and deal cards to them
	 * @param numHumanPlayers the number of interactive players
	 * @param numRandomNPCs   the number of NPCs that play only random cards
	 * @param numLegalNPCs the number of NPCs that play random, legal cards
	 * @param numSmartNPCs the number of smart NPCs
	 * @param AIThinkingTime  the time it takes an NPC to play a card
	 * @param numStartCards the number of cards in each player's hand
	 * @param winningScore the score points to win the game
	 * */
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
	 /** 
	  * @param Deal cards to the players
	  */
	public void dealCards() {
		Hand[] hands = deck.dealingOut(players.size(), numStartCards); // Last element of hands is leftover cards; these
																		// are
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setHand(hands[i]);
			players.get(i).getHand().sort(Hand.SortType.SUITPRIORITY, true);
		}
	}

	 /** 
	  * Start an round of Whist game. 
	  * @return an {@linkplain Optional<Integer>} that contains the id of the round winner
	  * or one that is empty if there is no winner and players are out of cards
	  */
	public Optional<Integer> playRound() {
		Integer roundWinner = null;

		// Choosing a random lead player on the first round
		Random random = new Random();
		activePlayer = players.get(random.nextInt(players.size()));

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
				activePlayer.setSelectedCard(null);
				selectedCard = null;
				do {
					notifyObserver(); // update UI messages
					selectedCard = activePlayer.playCard(trick, trump, lead);
				} while (selectedCard == null);

				// Set leading suit to the first player's suit
				if (trick.isEmpty())
					lead = (Poker.Suit) selectedCard.getSuit();

				// Check: Following card must follow suit if possible
				boolean isLegal = StrategyFactory.getInstance().getStrategy("DEFAULT").
						isLegal(activePlayer.getHand().getCardList(), trick.getCardList(), selectedCard, trump, lead);
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
			Card winningCard = StrategyFactory.getInstance().getStrategy("DEFAULT").
					winningCard(trick.getCardList(), lead, trump);
			trickWinner = trickWinner(players, winningCard);

			if (trickWinner != null) {
				activePlayer = trickWinner;

				// Update score
				System.out.println(" Winner: Player " + trickWinner.getId());
				System.out.println("-----------------------------------------");
				trickWinner.setScore(trickWinner.getScore() + 1);
				notifyObserver();

				// End the round if player reached winning score
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

	 /** 
	  * Determines the trick winner
	  * @param players all players in the game
	  * @param winningCard the card that won the trick
	  * @return the {@linkplain Player} that won the trick
	  */
	private Player trickWinner(ArrayList<Player> players, Card winningCard) {
		Player trickWinner = null;
		for (Player player : players) {
			if (player.getSelectedCard() == winningCard)
				trickWinner = player;
		}
		return trickWinner;
	}
	
	// ------------------- Utility Methods --------------------------
	 /** 
	  * Shift all elements of an {@linkplain ArrayList}. 
	  * <p>Elements that reached the end of the ArrayList will jump to the start</p>
	  * @param list the original ArrayList
	  * @param index the index of the element that needs to be at the start after shifting
	  * @return the shifted ArrayList
	  */
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
