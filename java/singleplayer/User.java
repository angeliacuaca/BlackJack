import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;

/**
 * Implementation of Users thread running from GameServer
 * 
 * @author angeliacuaca
 *
 */

public class User implements Serializable {

	User[] users;
	int userId;
	String userType;
	boolean isDone = false;

	// player's name
	private String playerName="";

	// cards in this player's hand, assume max 10
	private int maxNum = 10;
	private Card[] hand = new Card[maxNum];

	// number of cards in hand
	private int numCards;

	// if this player is already busted
	private boolean busted;

	// message to be printed to main program and log
	private String logMessage;

	/**
	 * Constructor for User. Each Users carry communication and gaming output
	 * stream also their own unique ID
	 * 
	 * @param commOut
	 *            output stream to communication log
	 * @param gameOut
	 *            output stream to gaming log
	 * @param out
	 *            output stream to clients
	 * @param in
	 *            data in from server that has been passed from clients
	 * @param user
	 *            array of Users connected
	 */
	public User(String type) {
		// this.commOut = commOut;
		// this.gameOut = gameOut;
		// this.dealerOut = dealerOut;
//		this.out = out;
//		this.in = in;
		// this.dealerIn = dealerIn;
		// this.user = user;
		// this.userId = userId;
		this.busted = false;
		this.userType = type;

		// each new players are given two-card hand
		emptyHand();
	}

//	Deck deck = new Deck();

	// Since only Dealer and Player on Single Player

	boolean eof = false;
	// indicate if the user finished their turn

	int[] result;

	public boolean isDone() {
		return this.isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	/**
	 * Add a card to player's hand
	 *
	 * @param newCard
	 *            card added to player's hand
	 * @return if the sum of new hand below or equal 21
	 */
	public boolean addCard(Card newCard) {

		logMessage = "Card given to " + playerName;

		if (numCards == maxNum) {
			System.out.print("Number of card exceed");
		}

		if (getHandSum() > 21) {
			logMessage = playerName + " BUSTED";
			busted = true;
		} else if (busted) {
			logMessage = "BUSTED: Can't add more card";
		} else {
			hand[numCards] = newCard;
			numCards++;
		}

		return (getHandSum() <= 21);
	}

	/**
	 * get the total of hand each player
	 * 
	 * @return number of total handsum
	 */
	public int getHandSum() {
		int handSum = 0;
		int cardNum = 0;
		int numAces = 0;

		for (int i = 0; i < numCards; i++) {

			// get number of current card
			cardNum = hand[i].getNumber();

			if (cardNum == 1) { // Ace
				numAces++;
				handSum += 11;
			} else if (cardNum > 10) { // Face card
				handSum += 10;

			} else {
				handSum += cardNum; // explicit number of the card
			}
		}

		// if player has aces and sum > 21, ace value becomes 1
		while (handSum > 21 && numAces > 0) {
			handSum -= 10;
			numAces--;
		}

		if (handSum > 21)
			busted = true;

		return handSum;
	}

	/**
	 * 
	 * @param showCard
	 *            show the card to the user
	 * @return formatted String all the cards belong to this player
	 * @throws IOException
	 */
	public String printHand(boolean showCard) throws IOException {

		String str = "\n" + getName() + "\'s cards: \n";

		// System.out.println("\n" + playerName + "\'s cards");
		for (int i = 0; i < 10; i++) {

			if (hand[i] != null) {
				str += hand[i] + "\n";
			}
		}

		str += "Total hand sum: " + getHandSum();
		if (busted) {
			str += "\n" + playerName + " BUSTED";
//			gameOut.writeUTF(playerName + " BUSTED");
		}
		return str;
	}

	// empty hand method to set
	public void emptyHand() {

		for (int i = 0; i < maxNum; i++) {

			// empty hand
			hand[i] = null;
		}
		numCards = 0;
	}

	// if this player is already busted
	public boolean isBusted() {
		return busted;
	}

	// get name of this player
	public String getName() {
		if (playerName == null || playerName.equals("")) {
			playerName = getType();
		}
		return playerName;
	}

	// set name of this player upon signing up
	public void setName(String newName) {
		playerName = newName;
	}

	// return string of any message updated
	public String printLog() {
		return logMessage;
	}

	// get type of this user
	public String getType() {
		return userType;
	}

	// total number of cards in hand
	public int getNumCards() {
		return numCards;
	}

}
