import java.io.*;
import java.net.*;
import java.util.Scanner;
/**
 * Implementation of Client type Dealer
 * @author angeliacuaca
 *
 */
public class Dealer {
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;
	static transient User dealer, player;

	static ObjectInputStream userIn;
	static ObjectOutputStream userOut;

	public static void main(String[] args) {
		System.out.println("Connecting...");

		//to stop the stream loop and game loop
		boolean eof = false;
		boolean gameStart = false;

		// indicate if the user finished their turn
		boolean playerDone = false;
		boolean dealerDone = false;

		//new deck took care by Dealer
		Deck deck = new Deck();

		try {

			//Connecting Socket and Input Output Streams
			socket = new Socket(InetAddress.getLoopbackAddress()
					.getHostAddress(), 06142);

			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			userIn = new ObjectInputStream(socket.getInputStream());
			userOut = new ObjectOutputStream(socket.getOutputStream());

			System.out.println("Connection Successful!");
			System.out.println("You're connected as DEALER");
			String msgin = "", msgout = "";

			while (!eof) {

				//if receive request from server
				if ((msgin = in.readUTF()).equals("All Player Signed up")) {
					System.out.println(msgin);

					dealer = (User) userIn.readObject();
					out.writeUTF("Dealer received " + dealer.getName()
							+ " Information from Server");

					player = (User) userIn.readObject();
					out.writeUTF("Dealer received " + player.getName()
							+ " Information from Server");

					out.writeUTF("Dealer requests Server to start the game");
					Thread.sleep(1000);
				}

				//if receive request from server
				if ((msgin = in.readUTF()).equals("START THE GAME")) {
					System.out.println("START THE GAME");
					gameStart = true;

					System.out.println("Dealer deals the cards");
					out.writeUTF("Dealer deals the cards");

					// DEALER DEAL THE CARD UPON REQUEST
					player.addCard(deck.dealNextCard());
					out.writeUTF(player.printLog());
					player.addCard(deck.dealNextCard());
					out.writeUTF(player.printLog());

					dealer.addCard(deck.dealNextCard());
					out.writeUTF(dealer.printLog());
					dealer.addCard(deck.dealNextCard());
					out.writeUTF(dealer.printLog());

					out.writeUTF("Cards are dealt");

					out.writeUTF("Dealer sending " + dealer.getName()
							+ " Information back to Server");

					userOut.writeObject(dealer);
					userOut.flush();

					out.writeUTF("Dealer sending " + player.getName()
							+ " Information back to Server");
					userOut.writeObject(player);
					userOut.flush();

					// // this print out the hand
					System.out.println(in.readUTF());

				}

				//Game start from here
				while (gameStart) {

					// player's turn
					while (!playerDone) {
						msgin = in.readUTF();
						System.out.println(msgin);

						//if receive request from server
						if (msgin.equals("CARD REQUESTED")) {

							player = (User) userIn.readObject();
							out.writeUTF("Dealer received " + player.getName()
									+ " Information from Server");

							System.out.println(in.readUTF());

							// add card to player's hand addCard method also
							// checked if the player has busted
							playerDone = !player.addCard(deck.dealNextCard());
							out.writeUTF(player.printLog());
							System.out.println(player.printLog());

							player.setDone(playerDone);

							if (player.isBusted()) {
								playerDone = true;
								player.setDone(true);
							}

							out.writeUTF("Dealer sending " + player.getName()
									+ " Request back to Server");
							userOut.writeObject(player);
							userOut.flush();

						}
						//if receive request from server
						if (msgin.equals("PLAYER DONE")) {

							System.out.println(in.readUTF());

							playerDone = true;

							player.setDone(playerDone);

						}


					}// end playerDone
					

					// dealer's turn
					while (!dealerDone) {

						System.out.println(dealer.getName()
								+ "'s TURN");
						dealer = (User) userIn.readObject();
						out.writeUTF("Dealer received "
								+ dealer.getName() + " Request from Server");

						if (dealer.getHandSum() < 17) {

							out.writeUTF(dealer.getName()
									+ " is taking more card ");
							System.out.println(dealer.getName()
									+ " is taking more card ");
							dealerDone = !dealer.addCard(deck.dealNextCard());
							dealer.setDone(dealerDone);
							System.out.println(dealer.printLog());
							System.out.println(dealer.printHand(true));

						} else {

							out.writeUTF(dealer.getName() + " Stays");
							System.out.println(dealer.getName() + " Stays");

							dealerDone = true;
							dealer.setDone(dealerDone);
						}

						out.writeUTF("Dealer sending " + dealer.getName()
								+ " Request back to Server");
						userOut.writeObject(dealer);
						userOut.flush();

					}// end while dealerDone

					if (playerDone && dealerDone) {
						out.writeUTF("ALL DONE");
						gameStart = false;
						playerDone=false;
						dealerDone=false;

						// // this print out the hand
						System.out.println(in.readUTF());
						System.out.println(in.readUTF());
						System.out.println(in.readUTF());
						System.out.println(in.readUTF());
					}

					// System.out.println();
				}// end gameStart

			}// end of eof

		} catch (EOFException e) {

			System.out.println("Server Terminating....");
			System.exit(0);
		} catch (ConnectException e) {
			System.out
					.println("Connection Refused due to exceeded users limit");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}