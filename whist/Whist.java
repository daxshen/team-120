
// Whist.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import observer.Observer;
import player.*;

import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("serial")
public class Whist extends CardGame implements Observer {

	// -------------------------------Attributes------------------------------------
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

	private final String version = "1.0";

	public int nbStartCards = 13;
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

	Font bigFont = new Font("Serif", Font.BOLD, 36);

	private Properties whistProperties;

	private Round round = null;

	//-------------------------------Utility Methods------------------------------------
	public boolean rankGreater(Card card1, Card card2) {
		return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
	}

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

	// Observer pattern
	@Override
	public void update() {
		updateGraphics();
	}

	//------------------------------- Game Properties------------------------------------
	/*
	 * This method initializes the game with default (original) properties Ensures
	 * basic properties are loaded irrespective or properties file
	 */
	private void initialiseProperties() {

		whistProperties = new Properties();
		whistProperties.setProperty("InteractivePlayer", "0");
		whistProperties.setProperty("RandomNpc", "3");
		whistProperties.setProperty("WinningScore", "11");
		whistProperties.setProperty("StartingCards", "13");
		whistProperties.setProperty("ThinkingTime", "2000");
		whistProperties.setProperty("EnforceRules", "false");

		try {
			readProperties();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		loadProperties();
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
		int humanPlayer = Integer.parseInt(whistProperties.getProperty("InteractivePlayer")) + 1;
		nbPlayers = Integer.parseInt(whistProperties.getProperty("RandomNpc")) + humanPlayer;
		winningScore = Integer.parseInt(whistProperties.getProperty("WinningScore"));
		nbStartCards = Integer.parseInt(whistProperties.getProperty("StartingCards"));
		thinkingTime = Integer.parseInt(whistProperties.getProperty("ThinkingTime"));
		enforceRules = Boolean.parseBoolean(whistProperties.getProperty("EnforceRules"));

		System.out.println("------------ Game Properties ------------");
		System.out.println("nbPlayers    : " + nbPlayers);
		System.out.println("winningScore : " + winningScore);
		System.out.println("thinkingTime : " + thinkingTime);
		System.out.println("enforceRules : " + enforceRules);
		System.out.println("-----------------------------------------");
	}

	//-------------------------------Graphics------------------------------------
	// Initialize graphics
	private void initGraphics() {

		removeAllActors();

		setTitle("THIS IS A WIP");
		setStatusText("Initializing...");

		// initialize sprites
		RowLayout[] layouts = new RowLayout[round.getPlayers().size()];

		for (int i = 0; i < round.getPlayers().size(); i++) {
			int id = round.getPlayerId(i);

			// Score sprites
			scoreActors[id] = new TextActor("0", Color.WHITE, bgColor, bigFont);
			addActor(scoreActors[id], scoreLocations[id]);

			// Hand card sprites
			layouts[id] = new RowLayout(handLocations[id], handWidth);
			layouts[id].setRotationAngle(90 * id);
			round.getPlayerById(id).getHand().setView(this, layouts[id]);
			round.getPlayerById(id).getHand().setTargetArea(new TargetArea(trickLocation));
			round.getPlayerById(id).getHand().draw();
		}

		// Trump sprites
		final Actor trumpsActor = new Actor("sprites/" + trumpImage[round.getTrump().ordinal()]);
		addActor(trumpsActor, trumpsActorLocation);
	}

	// Update graphics based on gameplay
	private void updateGraphics() {
		updateScoreGraphics();
		updateText();
		updateCardGraphics();
	}

	// TODO Comment
	private void updateScoreGraphics() {
		for (int i = 0; i < round.getPlayers().size(); i++) {
			// Update score sprites
			removeActor(scoreActors[round.getPlayerId(i)]);
			scoreActors[round.getPlayerId(i)] = new TextActor(String.valueOf(round.getPlayerScore(i)), Color.WHITE,
					bgColor, bigFont);
			addActor(scoreActors[round.getPlayerId(i)], scoreLocations[round.getPlayerId(i)]);
		}
	}

	// TODO Comment
	private void updateCardGraphics() {
		// Trick desktop card sprites

		// if (round.getTrickWinner() != null)
		// round.getTrick().setView(this, new RowLayout(hideLocation, 0));else
		round.getTrick().setView(this,
				new RowLayout(trickLocation, (round.getTrick().getNumberOfCards() + 2) * trickWidth));
		round.getTrick().draw();
		round.getTrick().setVerso(false);
	}

	// TODO Comment
	private void updateText() {
		// if (round.getActivePlayer().getSelectedCard() != null)
		// return;

		if (round.getTrickWinner() != null) {
			setStatusText("Player " + round.getTrickWinner().getId() + " wins trick.");
			delay(1500);
			return;
		}
		if (round.getActivePlayer().getSelectedCard() != null)
			return;
		else {
			setStatusText(round.getActivePlayer().getMessage());
			delay(round.getActivePlayer().getThinkingTime());
		}
	}

	//-------------------------------Main Game Loop------------------------------------
	public Whist() {
		super(700, 700, 30);

		initialiseProperties();

		round = new Round(deck, nbPlayers, thinkingTime, nbStartCards, winningScore);
		round.addObserver((observer.Observer) this); // Register as an observer to update graphics

		Optional<Integer> winner;
		do {
			initGraphics();
			winner = round.playRound();
		} while (!winner.isPresent());

		addActor(new Actor("sprites/gameover.gif"), textLocation);
		setStatusText("Game over. Winner is player: " + winner.get());
		refresh();

	}

	public static void main(String[] args) {
		// System.out.println("Working Directory = " + System.getProperty("user.dir"));
		new Whist();
	}

}
