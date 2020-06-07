package game;

// Whist.java

import ch.aplu.jcardgame.CardGame;
import ch.aplu.jcardgame.RowLayout;
import ch.aplu.jcardgame.TargetArea;
import ch.aplu.jgamegrid.*;
import observer.Observer;
import player.*;

import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * This class handles initlaisation of game properties and graphic components. 
 * It also observes {@linkplain Round} to update the graphics accordingly.
 * */
@SuppressWarnings("serial")
public class Whist extends CardGame implements Observer {

	// -------------------------------Attributes------------------------------------
	final String trumpImage[] = { "bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif" };

	private final String version = "1.0";

	private final int handWidth = 400;
	private final int trickWidth = 40;

	private final Location[] handLocations = { new Location(350, 625), new Location(75, 350), new Location(350, 75),
			new Location(625, 350) };
	private final Location[] scoreLocations = { new Location(575, 675), new Location(25, 575), new Location(575, 25),
			new Location(650, 575) };
	private Actor[] scoreActors = { null, null, null, null };
	private final Location trickLocation = new Location(350, 350);
	private final Location textLocation = new Location(350, 450);
	private Location hideLocation = new Location(-500, -500);
	private Location trumpsActorLocation = new Location(50, 50);

	private static final String propertyFile = new String("whist.properties");
	public void setStatus(String string) {
		setStatusText(string);
	}

	Font bigFont = new Font("Serif", Font.BOLD, 36);

	private Properties whistProperties;

	private Round round = null;

	//-------------------------------Interface Methods------------------------------------
	// Observer pattern
	@Override
	public void update() {
		updateGraphics();
	}

	//-------------------------------Game Properties------------------------------------
	/**
	 * This method initialises game properties which are based on the custom property file
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
		} catch (IOException e) {
			e.printStackTrace();
		}

		loadProperties();
	}

	/**
	 * This method reads the property file
	 * */
	private void readProperties() throws IOException {
		// Read properties
		FileReader inStream = null;
		try {
			inStream = new FileReader(propertyFile);
		} catch (FileNotFoundException e) {
			System.out.println("File not found : " + propertyFile);
			System.exit(0);
		}
		whistProperties.load(inStream);
		inStream.close();
	}

	/**
	 * This method converts the property file to attributes
	 * */
	private void loadProperties() {
		int numHumanPlayers = Integer.parseInt(whistProperties.getProperty("InteractivePlayers"));
		int numRandomNPCs = Integer.parseInt(whistProperties.getProperty("RandomNPCs"));
		int numLegalNPCs = Integer.parseInt(whistProperties.getProperty("LegalNPCs"));
		int numSmartNPCs = Integer.parseInt(whistProperties.getProperty("SmartNPCs"));
		int winningScore = Integer.parseInt(whistProperties.getProperty("WinningScore"));
		int nbStartCards = Integer.parseInt(whistProperties.getProperty("StartingCards"));
		int thinkingTime = Integer.parseInt(whistProperties.getProperty("ThinkingTime"));
		boolean enforceRules = Boolean.parseBoolean(whistProperties.getProperty("EnforceRules"));

		System.out.println("------------ Game Properties ------------");
		System.out.println("nbPlayers    : " + numRandomNPCs);
		System.out.println("winningScore : " + winningScore);
		System.out.println("thinkingTime : " + thinkingTime);
		System.out.println("enforceRules : " + enforceRules);
		System.out.println("-----------------------------------------");
		
		round = new Round(
				numHumanPlayers, 
				numRandomNPCs, 
				numLegalNPCs, 
				numSmartNPCs, 
				thinkingTime, 
				nbStartCards, 
				winningScore,
				enforceRules);
		round.addObserver((observer.Observer) this); // Register as an observer to update graphics
	}

	//-------------------------------Graphics------------------------------------
	/**
	 * This method initialises the graphic components of the game
	 * */
	private void initGraphics() {

		removeAllActors();

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

	/**
	 * This method updates the graphic components by observing the changes in {@linkplain Round}
	 * */	
	private void updateGraphics() {
		updateScoreGraphics();
		updateText();
		updateCardGraphics();
	}

	/**
	 * Updates the score number displayed
	 * */
	private void updateScoreGraphics() {
		for (int i = 0; i < round.getPlayers().size(); i++) {
			// Update score sprites
			removeActor(scoreActors[round.getPlayerId(i)]);
			scoreActors[round.getPlayerId(i)] = new TextActor(String.valueOf(round.getPlayerScore(i)), Color.WHITE,
					bgColor, bigFont);
			addActor(scoreActors[round.getPlayerId(i)], scoreLocations[round.getPlayerId(i)]);
		}
	}

	/**
	 * Updates the card sprites
	 * */
	private void updateCardGraphics() {

		if (round.getTrickWinner() != null)
			round.getTrick().setView(this, new RowLayout(hideLocation, 0));
		else
			round.getTrick().setView(this,
					new RowLayout(trickLocation, (round.getTrick().getNumberOfCards() + 2) * trickWidth));
		round.getTrick().draw();
		round.getTrick().setVerso(false);
	}

	/**
	 * Updates the status text 
	 * */
	private void updateText() {

		if (round.getTrickWinner() != null) {
			setStatusText("Player " + round.getTrickWinner().getId() + " wins trick.");
			delay(3000);
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
