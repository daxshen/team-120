
// Whist.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import player.*;

import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("serial")
public class WhistTest extends CardGame {

	ArrayList<Player> players;

	public enum Suit {
		SPADES, HEARTS, DIAMONDS, CLUBS
	}

	public enum Rank {
		// Reverse order of rank importance (see rankGreater() below)
		// Order of cards is tied to card images
		ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
	}

	final String trumpImage[] = { "bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif" };

	static final Random random = ThreadLocalRandom.current();

	// return random Enum value
	public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
		int x = random.nextInt(clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[x];
	}

	// return random Card from Hand
	public static Card randomCard(Hand hand) {
		int x = random.nextInt(hand.getNumberOfCards());
		return hand.get(x);
	}

	// return random Card from ArrayList
	public static Card randomCard(ArrayList<Card> list) {
		int x = random.nextInt(list.size());
		return list.get(x);
	}

	public boolean rankGreater(Card card1, Card card2) {
		return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
	}

	private final String version = "1.0";

	public final int nbStartCards = 4;
	private final int handWidth = 400;
	private final int trickWidth = 40;
	private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");

	private final Location[] handLocations = { new Location(350, 625), new Location(75, 350), new Location(350, 75),
			new Location(625, 350) };
	private final Location[] scoreLocations = { new Location(575, 675), new Location(25, 575), new Location(575, 25),
			new Location(650, 575) };
	private Actor[] scoreActors = { null, null, null, null };
	private final Location trickLocation = new Location(350, 350);
	private final Location textLocation = new Location(350, 450);
	private Hand[] hands;
	private Location hideLocation = new Location(-500, -500);
	private Location trumpsActorLocation = new Location(50, 50);

	/* Game properties */
	public int nbPlayers = 4;
	public int winningScore = 11;
	private int thinkingTime = 2000;
	private boolean enforceRules = false;

	public void setStatus(String string) {
		setStatusText(string);
	}

	private int[] scores = new int[nbPlayers];

	Font bigFont = new Font("Serif", Font.BOLD, 36);

	Properties whistProperties;

	private void initScore() {
		for (int i = 0; i < nbPlayers; i++) {
			scores[i] = 0;
			scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
			addActor(scoreActors[i], scoreLocations[i]);
		}
	}

	private void updateScore(int player) {
		removeActor(scoreActors[player]);
		scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
		addActor(scoreActors[player], scoreLocations[player]);
	}
	
	private void updateScore(Player player) {
		removeActor(scoreActors[player.getId()]);
		scoreActors[player.getId()] = new TextActor(String.valueOf(player.getScore()), Color.WHITE, bgColor, bigFont);
		addActor(scoreActors[player.getId()], scoreLocations[player.getId()]);
	}
	
	private Card selected;

	private void initPlayers(Hand[] hands) {
		players = new ArrayList<>(nbPlayers);

		for (int i = 0; i < nbPlayers; i++) {

			// TODO specify player type according to id
			Player player = null;
			if (i == 0) {
				player = new HumanPlayer(i, hands[i], thinkingTime);
				
			} else
				player = new ComputerPlayer(i, hands[i], thinkingTime);
			players.add(player);
		}

	}

	private void initRound() {

		hands = deck.dealingOut(nbPlayers, nbStartCards); // Last element of hands is leftover cards; these are ignored
		for (int i = 0; i < nbPlayers; i++) {
			hands[i].sort(Hand.SortType.SUITPRIORITY, true);
		}
		
		/*// Set up human player for interaction
		CardListener cardListener = new CardAdapter() // Human Player plays card
		{
			public void leftDoubleClicked(Card card) {
				selected = card;
				hands[0].setTouchEnabled(false);
			}
		};
		hands[0].addCardListener(cardListener);*/
		
		
		
		// graphics
		RowLayout[] layouts = new RowLayout[nbPlayers];
		for (int i = 0; i < nbPlayers; i++) {
			layouts[i] = new RowLayout(handLocations[i], handWidth);
			layouts[i].setRotationAngle(90 * i);
			// layouts[i].setStepDelay(10);
			hands[i].setView(this, layouts[i]);
			hands[i].setTargetArea(new TargetArea(trickLocation));
			hands[i].draw();
		}
		// for (int i = 1; i < nbPlayers; i++) // This code can be used to visually hide
		// the cards in a hand (make them face down)
		// hands[i].setVerso(true);
		// End graphics

		initPlayers(hands);

	}

	private Optional<Integer> playRound() { // Returns winner, if any
		// Select and display trump suit
		final Suit trumps = randomEnum(Suit.class);
		final Actor trumpsActor = new Actor("sprites/" + trumpImage[trumps.ordinal()]);
		addActor(trumpsActor, trumpsActorLocation);
		// End trump suit
		Hand trick;

		Card winningCard;
		Suit lead;
		Player currentPlayer;
		Player winner;

		int nextPlayer = random.nextInt(nbPlayers); // randomly select player to lead for this round
		for (int i = 0; i < nbStartCards; i++) {
			currentPlayer = players.get(nextPlayer);

			trick = new Hand(deck);
			selected = null;

			if (currentPlayer instanceof HumanPlayer) {
				currentPlayer.getHand().setTouchEnabled(true);
				// TODO change comment
				setStatus("Player 0 double-click on card to lead.");
				while (null == selected)
					delay(100);
			} else {
				setStatusText("Player " + currentPlayer.getId() + " thinking...");
				delay(thinkingTime);
				selected = currentPlayer.playCard();
			}

			// Lead with selected card
			trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
			trick.draw();
			selected.setVerso(false);
			// No restrictions on the card being lead
			lead = (Suit) selected.getSuit();
			selected.transfer(trick, true); // transfer to trick (includes graphic effect)
			winner = currentPlayer;
			winningCard = selected;

			// End Lead
			for (int j = 1; j < nbPlayers; j++) {
				if (++nextPlayer >= nbPlayers)
					nextPlayer = 0; // From last back to first
				selected = null;
				if (0 == nextPlayer) {
					hands[0].setTouchEnabled(true);
					setStatus("Player 0 double-click on card to follow.");
					while (null == selected)
						delay(100);
				} else {
					setStatusText("Player " + nextPlayer + " thinking...");
					delay(thinkingTime);
					selected = currentPlayer.playCard();
				}
				// Follow with selected card
				trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
				trick.draw();
				selected.setVerso(false); // In case it is upside down
				// Check: Following card must follow suit if possible
				if (selected.getSuit() != lead && hands[nextPlayer].getNumberOfCardsWithSuit(lead) > 0) {
					// Rule violation
					String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
					System.out.println(violation);
					if (enforceRules)
						try {
							throw (new BrokeRuleException(violation));
						} catch (BrokeRuleException e) {
							e.printStackTrace();
							System.out.println("A cheating player spoiled the game!");
							System.exit(0);
						}
				}
				// End Check
				selected.transfer(trick, true); // transfer to trick (includes graphic effect)
				System.out.println("winning: suit = " + winningCard.getSuit() + ", rank = " + winningCard.getRankId());
				System.out.println(" played: suit = " + selected.getSuit() + ", rank = " + selected.getRankId());
				if ( // beat current winner with higher card
				(selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
				// trumped when non-trump was winning
						(selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
					System.out.println("NEW WINNER");
					winner = currentPlayer;
					winningCard = selected;
				}
				// End Follow
			}
			delay(600);
			trick.setView(this, new RowLayout(hideLocation, 0));
			trick.draw();
			nextPlayer = winner.getId();
			setStatusText("Player " + nextPlayer + " wins trick.");
			scores[nextPlayer]++;
			updateScore(nextPlayer);
			if (winningScore == scores[nextPlayer])
				return Optional.of(nextPlayer);
		}
		removeActor(trumpsActor);
		return Optional.empty();
	}

	private Optional<Player> round() {

		// Select and display trump suit
		final Suit trumps = randomEnum(Suit.class);
		final Actor trumpsActor = new Actor("sprites/" + trumpImage[trumps.ordinal()]);
		addActor(trumpsActor, trumpsActorLocation);

		Player gameWinner = null;
		Player roundWinner = null;
		// Choosing a random lead player on the first round
		//Player nextPlayer = players.get(random.nextInt(nbPlayers));
		Player nextPlayer = players.get(0);


		// Keep playing the round till there is a winner
		while (gameWinner == null) {
			Hand trick = new Hand(deck);

			// Shift the active player to the head of the array
			players = shiftArray(players, players.indexOf(nextPlayer));
			Suit lead = null;

			// Each player plays a card
			for (Player player : players) {

				//TODO refactor: clear last round's selectedCard
				player.setSelectedCard(null);
				
				setStatusText(player.getMessage());
				delay(thinkingTime);

				//TODO Will cause infinite loop when player plays no card
				while (null == player.playCard())
					delay(100);
				
				//TODO refactor: lead selection
				if (players.indexOf(player) == 0)
					lead = (Suit) player.getSelectedCard().getSuit();
				
				//Draw card graphics 
				trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
				trick.draw();
				player.getSelectedCard().setVerso(false); 
				player.getSelectedCard().transfer(trick, true);
			}

			// Calculate result
			roundWinner = decideWinner(players, lead, trumps);
			if (roundWinner != null) {
				setStatusText("Player " + roundWinner.getId() + " wins trick.");
				delay(600);
				trick.setView(this, new RowLayout(hideLocation, 0));
				trick.draw(); 
				nextPlayer = roundWinner;
				roundWinner.setScore(roundWinner.getScore() + 1);
				updateScore(roundWinner);
				//End game if winner is born
				if (roundWinner.getScore() == winningScore)
					gameWinner = roundWinner;
			}
		}

		removeActor(trumpsActor);
		return Optional.empty();
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

	// TODO add javadoc comment
	private Player decideWinner(ArrayList<Player> players, Suit lead, Suit trump) {
		Player winner = players.get(0);
		Card winningCard = players.get(0).getSelectedCard();
		
		for (int i = 1; i < players.size(); i++) {
			Player player = players.get(i);
			if ( // beat current winner with higher card
			(player.getSelectedCard().getSuit() == winningCard.getSuit() && rankGreater(player.getSelectedCard(), winningCard)) ||
			// trumped when non-trump was winning
					(player.getSelectedCard().getSuit() == trump && winningCard.getSuit() != trump)) {
				System.out.println("NEW WINNER");
				winner = player;
			}
			
		}
		return winner;
	}

	/*
	 * This method initializes the game with default (original) properties Ensures
	 * basic properties are loaded irrespective or properties file
	 */
	private void initialiseProperties() {

		whistProperties.setProperty("InteractivePlayer", "0");
		whistProperties.setProperty("RandomNpc", "3");
		whistProperties.setProperty("WinningScore", "11");
		whistProperties.setProperty("ThinkingTime", "2000");
		whistProperties.setProperty("EnforceRules", "false");

		try {
			readProperties();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void readProperties() throws FileNotFoundException, IOException {
		// Read properties
		FileReader inStream = null;

		inStream = new FileReader("debug.properties");
		whistProperties.load(inStream);

		if (inStream != null) {
			inStream.close();
		}
	}

	private void loadProperties() {
		/*
		 * for(Map.Entry<Object, Object> entry : whistProperties.entrySet()) {
		 * System.out.println(entry.getKey().toString() + " : " +
		 * entry.getValue().toString()); }
		 */
		/*
		 * System.out.println("Orignal hardcoded properties:-");
		 * System.out.println("nbPlayers    : " + nbPlayers);
		 * System.out.println("winningScore : " + winningScore);
		 * System.out.println("thinkingTime : " + thinkingTime);
		 * System.out.println("enforceRules : " + enforceRules);
		 */
		int humanPlayer = Integer.parseInt(whistProperties.getProperty("InteractivePlayer")) + 1;
		nbPlayers = Integer.parseInt(whistProperties.getProperty("RandomNpc")) + humanPlayer;
		winningScore = Integer.parseInt(whistProperties.getProperty("WinningScore"));
		thinkingTime = Integer.parseInt(whistProperties.getProperty("ThinkingTime"));
		enforceRules = Boolean.parseBoolean(whistProperties.getProperty("EnforceRules"));

		System.out.println("File read properties:-");
		System.out.println("nbPlayers    : " + nbPlayers);
		System.out.println("winningScore : " + winningScore);
		System.out.println("thinkingTime : " + thinkingTime);
		System.out.println("enforceRules : " + enforceRules);

	}

	public WhistTest() {
		super(700, 700, 30);

		whistProperties = new Properties();
		initialiseProperties();
		loadProperties();

		setTitle("THIS IS A TEST");
		setStatusText("Initializing...");
		initScore();
		// Optional<Integer> winner;
		Optional<Player> winner;

		do {
			initRound();
			winner = round();
		} while (!winner.isPresent());
		addActor(new Actor("sprites/gameover.gif"), textLocation);
		setStatusText("Game over. Winner is player: " + winner.get());
		refresh();

	}

	public static void main(String[] args) {
		// System.out.println("Working Directory = " + System.getProperty("user.dir"));
		new WhistTest();
	}

}
