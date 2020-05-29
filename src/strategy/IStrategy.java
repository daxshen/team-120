package strategy;

import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import game.Poker.Suit;

public interface IStrategy {
	
	public Card execute(
			ArrayList<Card> hand, 
			ArrayList<Card> trick, 
			Suit trump, 
			Suit lead);
}