import java.io.Serializable;



/**
 * Implementation of Card
 * 
 * @author angeliacuaca
 *
 */
public class Card implements Serializable{

	/**
	 * One of 4 suits
	 */
	private Suit cardSuit;

	/**
	 * Number of card, A is 1 OR 11 J,Q,K is 10 No Joker
	 */
	private int cardNumber;

	/**
	 * Card Constructor
	 * 
	 * @param suit
	 *            suit of the card
	 * @param num
	 *            number of the card
	 */
	public Card(Suit suit, int num)  {

		this.cardSuit = suit;
		this.cardNumber = num;
	}

	// Getter and Setter
	public int getNumber() {
		return cardNumber;
	}

	public Suit getSuit() {
		return cardSuit;
	}

	/**
	 * Convert number to letter
	 */
	public String toString() {

		String numStr = "none";

		switch (cardNumber) {
		case 1:
			numStr = "A";
			break;
		case 11:
			numStr = "J";
			break;
		case 12:
			numStr = "Q";
			break;
		case 13:
			numStr = "K";
			break;
		default:
			numStr = "" + getNumber();
		}

		return cardSuit + "\t " + numStr;
	}
}
