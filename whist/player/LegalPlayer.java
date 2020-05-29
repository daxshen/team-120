package player;

import ch.aplu.jcardgame.Hand;

public class LegalPlayer extends ComputerPlayer {

	public LegalPlayer(int id, Hand hand, int thinkingTime) {
		//Legal strategy
		super(id, hand, thinkingTime);
	}

}
