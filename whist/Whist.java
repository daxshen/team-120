
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


	Font bigFont = new Font("Serif", Font.BOLD, 36);

	private Properties whistProperties;

	private Round round = null;
	

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

		System.out.println("File read properties:-");
		System.out.println("nbPlayers    : " + nbPlayers);
		System.out.println("winningScore : " + winningScore);
		System.out.println("thinkingTime : " + thinkingTime);
		System.out.println("enforceRules : " + enforceRules);

	}

	//Initialize graphics
	private void initGraphics() {
		
		setTitle("THIS IS A TEST2");
		setStatusText("Initializing...");
	
		//initialize sprites
		RowLayout[] layouts = new RowLayout[round.getPlayers().size()];
		for (int i = 0; i < round.getPlayers().size(); i++) {
			Player player = round.getPlayers().get(i);
			
			//Score sprites
			scoreActors[player.getId()] = new TextActor("0", Color.WHITE, bgColor, bigFont);
			addActor(scoreActors[player.getId()], scoreLocations[player.getId()]);
			
			//Hands sprites
			layouts[player.getId()] = new RowLayout(handLocations[player.getId()], handWidth);
			layouts[player.getId()].setRotationAngle(90 * i);
			player.getHand().setView(this, layouts[i]);
			player.getHand().setTargetArea(new TargetArea(trickLocation));
			player.getHand().draw();
		}
		
		//Trump sprites
		final Actor trumpsActor = new Actor("sprites/" + trumpImage[round.getTrump().ordinal()]);
		addActor(trumpsActor, trumpsActorLocation);
	}
	

	//Update graphics based on gameplay
	private void updateGraphics() {
		updateScoreGraphics();
		updateText();
		updateHandGraphics();
	}
	
	//TODO Comment
	private void updateScoreGraphics() {			
		for (int i = 0; i < round.getPlayers().size(); i ++) {
			//Update score sprites
			Player player  = round.getPlayers().get(i);
			removeActor(scoreActors[player.getId()]);
			scoreActors[player.getId()] = new TextActor(String.valueOf(player.getScore()), Color.WHITE, bgColor, bigFont);
			addActor(scoreActors[player.getId()], scoreLocations[player.getId()]);			
		}
	}
	
	//TODO Comment
	private void updateHandGraphics() {
		if (round.getWinner() != null)
			round.getTrick().setView(this, new RowLayout(hideLocation, 0));
		else
			round.getTrick().setView(this, new RowLayout(trickLocation, (round.getTrick().getNumberOfCards() + 2) * trickWidth));
		round.getTrick().draw();
		round.getTrick().setVerso(false);
	}
	
	//TODO Comment
	private void updateText() {
		if (round.getWinner() != null) {
			setStatusText("Player " + round.getWinner().getId() + " wins trick.");
			delay(600);		
		}
		else {
			setStatusText(round.getNextPlayer().getMessage());
			delay(round.getNextPlayer().getThinkingTime());
		}
	}
	
	public Whist() {
		super(700, 700, 30);

		initialiseProperties();
		
		round = new Round(deck, nbPlayers, thinkingTime, nbStartCards, winningScore);
		round.addObserver((observer.Observer)this); //Register as an observer to update graphics
		initGraphics();
		//Optional<Player> winner;
		Player winner = null;
		do {
			winner = round.playRound();
		} while (winner == null);
		addActor(new Actor("sprites/gameover.gif"), textLocation);
		setStatusText("Game over. Winner is player: " + winner.getId());
		refresh();

	}
	
	public static void main(String[] args) {
		// System.out.println("Working Directory = " + System.getProperty("user.dir"));
		new Whist();
	}

	//Observer pattern
	@Override
	public void update() {
		updateGraphics();
	}

}
