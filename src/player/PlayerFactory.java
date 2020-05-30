package player;

import java.util.ArrayList;

import player.*;

// TODO Add comments
public class PlayerFactory {
	// ------------------- Attributes ---------------------
	private static PlayerFactory instance = null;

	// ------------------- Getters & Setters -------------------
	public static PlayerFactory getInstance() {
		if (instance == null)
			instance = new PlayerFactory();
		return instance;	
	}

	// ------------------- Methods ------------------------
	//TODO add comment
	public ArrayList<Player> generatePlayers(int numHumanPlayers, int numRandomNPCs, int numLegalNPCs, int numSmartNPCs,
			int AIThinkingTime) {
		
		ArrayList<Player> players = new ArrayList<>();
		for (int i = 0; i < numHumanPlayers; i++) {
			//Human player will always have id 0
			players.add(getPlayer(i, AIThinkingTime, "HUMAN"));
		}
		
		for (int i = 0; i < numRandomNPCs; i++) {
			players.add(getPlayer(players.size(), AIThinkingTime, "RANDOM"));
		}
		
		for (int i = 0; i < numLegalNPCs; i++) {
			players.add(getPlayer(players.size(), AIThinkingTime, "LEGAL"));
		}
		
		for (int i = 0; i < numSmartNPCs; i++) {
			players.add(getPlayer(players.size(), AIThinkingTime, "SMART"));
		}	
		return players;
	}

	//TODO add comment
	public Player getPlayer(int id, int thinkingTime, String playStyle) {

		if (playStyle.toUpperCase() == "HUMAN")
			return new HumanPlayer(id);
		else
			return new ComputerPlayer(id, thinkingTime, playStyle);
	}
}
