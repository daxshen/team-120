package player;

import java.util.ArrayList;

import player.*;

/** This class handles the creation logic of {@linkplain Player}s */
public class PlayerFactory {
	// ------------------- Attributes ---------------------
	/** This singleton instance of {@linkplain PlayerFactory */
	private static PlayerFactory instance = null;

	
	// ------------------- Getters & Setters -------------------
	/** @return the singleton instance of {@linkplain PlayerFactory */
	public static PlayerFactory getInstance() {
		if (instance == null)
			instance = new PlayerFactory();
		return instance;
	}

	
	// ------------------- Methods ------------------------
	/**
	 * Generates {@linkplain Player}s based on the game properties
	 * 
	 * @param numHumanPlayers the number of interactive players
	 * @param numRandomNPCs   the number of NPCs that play only random cards
	 * @param numLegalNPCs    the number of NPCs that play random, legal cards
	 * @param numSmartNPCs    the number of smart NPCs
	 * @param AIThinkingTime  the time it takes an NPC to play a card
	 * @return an {@linkplain ArrayList} of all {@linkplain Player}s in the game
	 */
	public ArrayList<Player> generatePlayers(int numHumanPlayers, int numRandomNPCs, int numLegalNPCs, int numSmartNPCs,
			int AIThinkingTime) {

		ArrayList<Player> players = new ArrayList<>();
		for (int i = 0; i < numHumanPlayers; i++) {
			// Human player will always have id 0
			players.add(getPlayer(i, AIThinkingTime, "HUMAN"));
		}

		for (int i = 0; i < numSmartNPCs; i++) {
			players.add(getPlayer(players.size(), AIThinkingTime, "SMART"));
		}
		
		for (int i = 0; i < numRandomNPCs; i++) {
			players.add(getPlayer(players.size(), AIThinkingTime, "RANDOM"));
		}

		for (int i = 0; i < numLegalNPCs; i++) {
			players.add(getPlayer(players.size(), AIThinkingTime, "LEGAL"));
		}

		return players;
	}

	/**
	 * Generates one {@linkplain Player}
	 * @param id the ID of the {@linkplain Player}
	 * @param thinkingTime the time it takes the player to play a {@linkplain Card}
	 * @param playStyle the type of {@linkplain Player} (e.g. "SMART", "RANDOM")
	 */
	public Player getPlayer(int id, int thinkingTime, String playStyle) {

		if (playStyle.toUpperCase() == "HUMAN")
			return new HumanPlayer(id);
		else
			return new ComputerPlayer(id, thinkingTime, playStyle);
	}
}
