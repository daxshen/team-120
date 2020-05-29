package player;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ch.aplu.jcardgame.Card;
import game.Poker;
import strategy.PlayStrategy;

public class ComputerPlayer extends Player {
	//------------------- Constructors -------------------
	public ComputerPlayer(int id, int thinkingTime, PlayStrategy playStrategy) {
		super(id, thinkingTime, playStrategy);
		this.message = "Player " + id + " thinking...";
	}
	
}
