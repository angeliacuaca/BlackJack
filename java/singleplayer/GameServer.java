import java.io.*;
import java.net.*;

/**
 * Implementation of Server, run this program first
 * 
 * @author angeliacuaca
 *
 */
public class GameServer {
	// Declarations of connetion uses
	static ServerSocket clientServer, dealerServer, commServer, gameServer;
	static Socket clientSocket, dealerSocket, commSocket, gameSocket,
			userSocket;
	static DataOutputStream playerOut, commOut, gameOut, dealerOut;
	static DataInputStream playerIn, dealerIn;
	static User[] user = new User[2]; // num of users for Single player is 2
	static User player, dealer;

	// Object input output for serializable
	static ObjectOutputStream userOut;
	static ObjectInputStream userIn;

	public static void main(String[] args) {

		// to stop the stream loop and game loop
		boolean eof = false;
		boolean gameStart = false;

		// indicate if the user finished their turn
		boolean playerDone = false;
		boolean dealerDone = false;

		try {

			// Player uses port 16142
			clientServer = new ServerSocket(16142);

			// Communication Log uses port 26142
			commServer = new ServerSocket(26142);
			commSocket = commServer.accept();
			commOut = new DataOutputStream(commSocket.getOutputStream());

			commOut.writeUTF("Starting Server...");
			commOut.writeUTF("Server Started...");

			// Gaming Log uses port 36142
			gameServer = new ServerSocket(36142);
			gameSocket = gameServer.accept();
			gameOut = new DataOutputStream(gameSocket.getOutputStream());

			// Dealer uses port 06142
			dealerServer = new ServerSocket(06142);

			String fromPlayer = "", toPlayer = "", fromDealer = "", toDealer = "";

			while (!eof) {

				for (int i = 0; i < user.length; i++) {

					if (user[i] == null) {

						// user number 0 is always a dealer
						if (i == 0) {

							dealerSocket = dealerServer.accept();
							dealerIn = new DataInputStream(
									dealerSocket.getInputStream());
							dealerOut = new DataOutputStream(
									dealerSocket.getOutputStream());

							// dealer takes care of player object for cards
							userOut = new ObjectOutputStream(
									dealerSocket.getOutputStream());
							userIn = new ObjectInputStream(
									dealerSocket.getInputStream());

							dealer = new User("DEALER");

							user[i] = dealer;
							commOut.writeUTF(user[i].getType() + " signed in ");

							// closed after 1 dealer
							dealerServer.close();

							commOut.writeUTF(user[i].getType()
									+ " Connecting from: "
									+ dealerSocket.getInetAddress());

							break;
						} else {
							clientSocket = clientServer.accept();

							playerOut = new DataOutputStream(
									clientSocket.getOutputStream());
							playerIn = new DataInputStream(
									clientSocket.getInputStream());

							player = new User("PLAYER");

							user[i] = player;

							commOut.writeUTF(user[i].getType() + " signed in ");

							// closed after 1 player
							clientServer.close();

							// Thread thread = new Thread(user[i]);
							// thread.start();

							commOut.writeUTF(user[i].getType()
									+ " Connecting from: "
									+ clientSocket.getInetAddress());

							break;

						}// end if user[0]
					}// end if user null
				} // end for

				// if dealer exist
				if (dealer != null) {

					// if player exist
					if (player != null) {

						dealerOut.writeUTF("All Player Signed up");
						commOut.writeUTF("All Player Signed up");

						commOut.writeUTF("Dealer requests " + dealer.getName()
								+ " Information");
						commOut.writeUTF("Server sending " + dealer.getName()
								+ " Information to Dealer");
						userOut.writeObject(dealer);
						userOut.flush();
						commOut.writeUTF(dealerIn.readUTF());// receive

						commOut.writeUTF("Dealer requests " + player.getName()
								+ " Information");
						commOut.writeUTF("Server sending " + player.getName()
								+ " Information to Dealer");
						userOut.writeObject(player);
						userOut.flush();
						commOut.writeUTF(dealerIn.readUTF());// receive

						if ((fromDealer = dealerIn.readUTF())
								.equals("Dealer requests Server to start the game")) {

							commOut.writeUTF("\n" + fromDealer);
							commOut.writeUTF("Server receives Dealer request to start game");

							commOut.writeUTF("\nSERVER START THE GAME");
							gameOut.writeUTF(fromDealer);

							broadCast("START THE GAME");

							gameOut.writeUTF("Game Start");

							gameStart = true;
							fromDealer = dealerIn.readUTF();
							gameOut.writeUTF(fromDealer);
							playerOut.writeUTF(fromDealer);

							// log on card dealing received from dealer
							gameOut.writeUTF(dealerIn.readUTF());
							gameOut.writeUTF(dealerIn.readUTF());
							gameOut.writeUTF(dealerIn.readUTF());
							gameOut.writeUTF(dealerIn.readUTF());
						}
						// game starts here
						while (gameStart) {

							// report from dealer the card has finished dealt
							if ((fromDealer = dealerIn.readUTF())
									.equals("Cards are dealt")) {

								commOut.writeUTF("Dealer Report finished deal cards");

								commOut.writeUTF("Server Requests player's information for printing hand");

								commOut.writeUTF(dealerIn.readUTF());
								dealer = (User) userIn.readObject();
								commOut.writeUTF("Server received "
										+ dealer.getName()
										+ " Information from Dealer");

								commOut.writeUTF(dealerIn.readUTF());
								player = (User) userIn.readObject();
								commOut.writeUTF("Server received "
										+ player.getName()
										+ " Information from Dealer");

								// indicate if the user finished their turn
								playerDone = false;
								dealerDone = false;

								gameOut.writeUTF("Cards are dealt\n");

								// print player's hand on player's screen
								playerOut.writeUTF(player.printHand(true));
								dealerOut.writeUTF(dealer.printHand(true));

								commOut.writeUTF("Server prints out hands");
							}

							// SERVER ORCHERSTRATING THE GAME
							while (!player.isDone() || !dealer.isDone()) {

								// ask user input
								while (!player.isDone()) {

									gameOut.writeUTF(player.getName()
											+ "'s TURN");
									playerOut.writeUTF(player.getName()
											+ "'s TURN");

									playerOut
											.writeUTF("Hit or Stay? (Enter H or S): ");

									// waiting input from client
									fromPlayer = playerIn.readUTF();

									if (fromPlayer.equalsIgnoreCase("H")) {

										// inform dealer this user request card
										dealerOut.writeUTF("CARD REQUESTED");
										commOut.writeUTF(player.getName()
												+ " Requests for card");
										commOut.writeUTF("Server sending "
												+ player.getName()
												+ " Request to Dealer");
										userOut.writeObject(player);
										userOut.flush();
										commOut.writeUTF(dealerIn.readUTF());

										// send Request to gaming log
										gameOut.writeUTF(player.getName()
												+ " is asking for more card ");

										// inform all players
										broadCast(player.getName()
												+ " is asking for more card ");
										playerOut.writeUTF(dealer.printLog());

										gameOut.writeUTF(dealerIn.readUTF());

										commOut.writeUTF(dealerIn.readUTF());
										player = (User) userIn.readObject();
										commOut.writeUTF("Server received "
												+ player.getName()
												+ " Request from Dealer");

										gameOut.writeUTF(player.printLog());

										// print player's hand on player's
										// screen
										playerOut.writeUTF(player
												.printHand(true));

										if (player.isDone() == true) {
											gameOut.writeUTF(player.getName()
													+ " has finished");
										}

									} else if (fromPlayer.equalsIgnoreCase("S")) {

										dealerOut.writeUTF("PLAYER DONE");

										// inform all players
										broadCast(player.getName() + " Stays ");

										gameOut.writeUTF(player.getName()
												+ " Stays ");
										gameOut.writeUTF(player.printLog());

										playerDone = true;
										// player opt to no additional cards
										player.setDone(playerDone);

										gameOut.writeUTF(player.getName()
												+ " has finished");

									} else {

										// send Request to communication log
										commOut.writeUTF("ERROR: "
												+ player.getName()
												+ " Entered invalid input");

										commOut.writeUTF("Request Input from "
												+ player.getName());

										// request player to re enter the input
										playerOut
												.writeUTF("ERROR: Invalid input");
									}

								}// end player's turn

								// dealer's turn
								// dealer operation running on dealer's class
								if (!dealer.isDone() && player.isDone()) {

									gameOut.writeUTF("DEALER's TURN");
									playerOut.writeUTF("\nDEALER's TURN");

									commOut.writeUTF("Server sending "
											+ dealer.getName()
											+ " Request to Dealer");
									userOut.writeObject(dealer);
									userOut.flush();
									commOut.writeUTF(dealerIn.readUTF());

									fromDealer = dealerIn.readUTF();
									// print if dealer hits or stays
									gameOut.writeUTF(fromDealer);
									gameOut.writeUTF(dealer.printLog());
									playerOut.writeUTF(fromDealer);
									playerOut.writeUTF(dealer.printLog());

									commOut.writeUTF(dealerIn.readUTF());
									dealer = (User) userIn.readObject();
									commOut.writeUTF("Server received "
											+ dealer.getName()
											+ "  from Dealer");

									Thread.sleep(1000);

								}// end dealer's turn

								commOut.writeUTF("All Players has finished their moves");
							}// end both turn

							// if dealer report all player's done
							if ((fromDealer = dealerIn.readUTF())
									.equals("ALL DONE")) {

								gameOut.writeUTF(dealer.getName()
										+ " has finished");

								gameOut.writeUTF("All Players has finished their moves");
								commOut.writeUTF("Server Calculating the result");

								broadCast("\n----------GAME OVER! Result: ");
								broadCast(player.printHand(true));
								broadCast(dealer.printHand(true));

								int playerSum = player.getHandSum();
								int dealerSum = dealer.getHandSum();

								gameOut.writeUTF(player.getName() + " Scored: "
										+ player.getHandSum());
								gameOut.writeUTF(dealer.getName() + " Scored: "
										+ dealer.getHandSum());
								System.out.println();

								// if sums are equal or all players busted
								if ((playerSum == dealerSum)
										|| (player.isBusted() && dealer
												.isBusted())) {
									broadCast("\n" + "DRAW \n");
									gameOut.writeUTF("[GAME RESULT] DRAW \n");
								} else {

									// if player sum larger than dealer and
									// player is not busted
									if (((playerSum > dealerSum) && !player
											.isBusted()) || dealer.isBusted()) {
										gameOut.writeUTF("[GAME RESULT] "
												+ player.getName() + " WIN!\n");
										broadCast("\n" + player.getName()
												+ " WIN!\n");
										// if dealer sum larger than player and
										// dealer is not busted
									} else if (((playerSum < dealerSum) && !dealer
											.isBusted()) || player.isBusted()) {
										gameOut.writeUTF("[GAME RESULT] "
												+ dealer.getName() + " WIN\n");
										broadCast("\n" + dealer.getName()
												+ " WIN\n");
									}
								}
								commOut.writeUTF("Result printed on Players' screens");
								commOut.writeUTF("-----------Game End----------\n");
								gameOut.writeUTF("-----------Game End----------\n");
								gameStart = false;
								eof = true;
							}

							playerOut.flush();
							dealerOut.flush();

						}// end gameStart
						playerOut.writeUTF("Continue? (Enter Y or N): ");

						// waiting input from client
						fromPlayer = playerIn.readUTF();

						if (fromPlayer.equalsIgnoreCase("Y")) {
							gameStart = true;
							eof = false;
							playerDone = false;
							dealerDone = false;
							player = new User("PLAYER");
							dealer = new User("DEALER");
						} else {
							System.exit(0);
						}
					}// end if player not null

				}// end if dealer not null

			}// end eof

		} catch (EOFException e) {
			System.out.println("RESET GAME");
			eof = true;
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void broadCast(String message) throws Exception {
		dealerOut.writeUTF(message);
		playerOut.writeUTF(message);
	}

}