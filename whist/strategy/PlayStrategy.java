package strategy;
import java.util.ArrayList;
import ch.aplu.jcardgame.Card;

public interface PlayStrategy {

	public Card playCard(ArrayList<Card> hand, ArrayList<Card> playedCards);
}
