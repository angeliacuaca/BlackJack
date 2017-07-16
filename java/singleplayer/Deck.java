import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Implement of a deck of cards, cards dealt are of infinitive numbers
 * @author angeliacuaca
 */
public class Deck implements Serializable{

	// array of card
	private Card[] cards;

	// each deck has 52 cards
	private int numCards = 52;

	/**
	 * Constructor of Deck
	 * 
	 * @param numDecks
	 *            number of decks (infinite)
	 * @param shuffle
	 *            if the card is already shuffled
	 */
	public Deck(boolean shuffle) {

		createNewDeck(shuffle);

		if (shuffle)
			shuffle();

	}

	/**
	 * Create new deck 
	 * @param shuffle	if new deck is shuffled
	 */
	public void createNewDeck(boolean shuffle) {
		// each deck has 52 cards
		this.cards = new Card[numCards];

		int i = 0;

		// each deck has 4 suits
		for (int suit = 0; suit < 4; suit++) {

			// each suit has 13 number cards
			for (int num = 1; num <= 13; num++) {

				// // create new card
				cards[i] = new Card(Suit.values()[suit], num);
				i++;
			}
		}

		if (shuffle)
			shuffle();

	}

	/**
	 * Overloading to set default deck
	 */
	public Deck() {
		this(true);
	}

	/**
	 * Randomise cards dealt
	 */
	public void shuffle() {

		// random number generator
		Random random = new Random();

		// temporary card to swap
		Card tempCard;

		// temporary index when swapping
		int tempIndex;

		for (int i = 0; i < numCards; i++) {

			tempIndex = random.nextInt(numCards);

			// do swap
			tempCard = cards[i];
			cards[i] = cards[tempIndex];
			cards[tempIndex] = tempCard;
		}
	}

	/**
	 * Deal next card from the top of the deck
	 */
	public Card dealNextCard() {

		Card topCard = cards[0];

		// shuffle the deck
		shuffle();

		return topCard;
	}
	
	/**
	 * Method called to time log
	 * @return print current date and time
	 */
	public String logTime() {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String logTime = df.format(new Date()) + ": ";
		return logTime;
	}
}
