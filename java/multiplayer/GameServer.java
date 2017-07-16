import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.io.*;

public class GameServer {

	static ServerSocket clientSocket, commSocket, gameSocket;
	static Socket socket, commLog, gameLog;
	static DataOutputStream out, commOut, gameOut, dealerOut;
	static DataInputStream in, dealerIn;
	static Users[] user = new Users[6];
	static ServerSocket dealerServer;
	static Socket dealerSocket;
	static Users dealer;

	public static void main(String[] args) throws Exception {

		// User uses port 16142
		clientSocket = new ServerSocket(16142);

		// Communication Log uses port 26142
		commSocket = new ServerSocket(26142);
		commLog = commSocket.accept();
		commOut = new DataOutputStream(commLog.getOutputStream());

		commOut.writeUTF("Starting Server...");
		commOut.writeUTF("Server Started...");

		// Gaming Log uses port 36142
		gameSocket = new ServerSocket(36142);
		gameLog = gameSocket.accept();
		gameOut = new DataOutputStream(gameLog.getOutputStream());

		// Dealer uses port 06142
		// dealerServer = new ServerSocket(06142);
		// dealerSocket = dealerServer.accept();
		// dealerOut = new DataOutputStream(dealerSocket.getOutputStream());
		// dealerIn = new DataInputStream(dealerSocket.getInputStream());

		Deck deck = new Deck();

		boolean eof = false;
		boolean playerDone = false;
		boolean dealerDone = false;

		boolean gameStart = false;
		int[] result;

		while (!eof) {
			boolean gameFinished = true;
			try {

				// Accept any client connected but only keep 2 users,
				// disconnected clients will be replaced by waiting one
				for (int i = 0; i < user.length; i++) {

					if (user[i] == null) {
						socket = clientSocket.accept();

						out = new DataOutputStream(socket.getOutputStream());
						in = new DataInputStream(socket.getInputStream());

						user[i] = new Users(commOut, gameOut, out, in, user, i,
								"PLAYER", 0);

						if (i != 0) {
							user[i].out.writeUTF("Enter Your Name :");
							String name = in.readUTF();
							user[i].setName(name.toUpperCase());
						}

						Thread thread = new Thread(user[i]);
						thread.start();

						commOut.writeUTF(user[i].getName()
								+ " Connecting from: "
								+ socket.getInetAddress());

						// For single player only need dealer and player
						// Any additional connection will be refused
						// if (isFull()) {
						// clientSocket.close();
						// }
					}
					System.out.println(i);
					Thread.sleep(1000);

				} // end for

				if (!gameStart) {
					System.out.println("Game will be starting in 5 seconds");
					informAll("Game will be starting in 5 seconds");
					for (int x = 5; x > 0; x--) {
						System.out.println(x);
						informAll("" + x);
						Thread.sleep(1000);
					}

					for (int i = 1; i < user.length; i++) {
						if (user[i] != null)
							System.out.println(user[i].getName() + " " + i);
					}

					gameStart = true;
				}

				// all player filled in
				for (int i = 0; i < user.length; i++) {
					if (user[0] != null && user[i] != null) {

						System.out.println(i);

						gameOut.writeUTF("Game Start");

						// player = user[1];
						dealer = user[0];
						// gameStart here
						String message;

						if (i == 0) {
							user[0].setName("DEALER");
						} else {
							if (user[i].getName().equals("PLAYER")) {
								user[i].setName("PLAYER " + i);
							}
						}
					}

					user[i].out.writeUTF("YOU ARE PLAYING AS "
							+ user[i].getName());

					user[i].addCard(deck.dealNextCard());
					gameOut.writeUTF(user[i].printLog());
					user[i].addCard(deck.dealNextCard());
					gameOut.writeUTF(user[i].printLog());

					// indicate if the user finished their turn
					user[i].setDone(false);

					gameOut.writeUTF("Cards are dealt\n");

					// print player's hand on player's screen
					user[i].out.writeUTF(user[i].printHand(true));

				}

				if (gameStart) {
					// // dealer gets the cards
					// if (user[0] != null && user[1] != null) {

					// user[0].addCard(deck.dealNextCard());
					// gameOut.writeUTF(user[0].printLog());
					// user[0].addCard(deck.dealNextCard());
					// gameOut.writeUTF(user[0].printLog());
					// user[0].out.writeUTF(user[0].printHand(true));
					//
					// }
playerDone = false;
					for (int i = 1; i < user.length; i++) {

						if (user[i] != null) {
							// sequence turn
							while (!user[i].isDone() || !user[0].isDone()) {
								
								if (!user[i].isDone()) {

									// Process player's move on Users class
									playerDone = user[i].isDone();

								} else {
									playerDone = true;

								}// end player's turn


								// dealer's turn
								if (!user[0].isDone() && playerDone == true) {

									gameOut.writeUTF("DEALER's TURN\n");
									informAll("DEALER's TURN\n");
									if (user[0].getHandSum() < 17) {

										gameOut.writeUTF("DEALER is taking more card");

										informAll("\nDEALER is taking more card");

										dealerDone = !user[0].addCard(deck
												.dealNextCard());
										gameOut.writeUTF(user[0].printLog());

										user[0].setDone(dealerDone);
										// show dealer's hand
										user[0].out.writeUTF(user[0]
												.printHand(true));
									} else {
										gameOut.writeUTF(user[0].getName()
												+ " stays");
										informAll("\nDEALER stays");
										// show dealer's hand
										user[0].out.writeUTF(user[0]
												.printHand(true));
										user[0].setDone(true);
										dealerDone = true;
										playerDone=true;
									}
								}// end dealer's turn

							}// end both turn
						}// when user not null
					}// for loop after sign in
						// eof = true;

					if (playerDone && dealerDone) {
						informAll("----------GAME OVER! Result: ");
						for (int i = 0; i < user.length; i++) {

							if (user[i] != null) {
								informAll(user[i].printHand(true));
								// informAll(user[0].printHand(true));

								informAll(user[i].getName() + " Scored: "
										+ user[i].getHandSum());

								gameOut.writeUTF(user[i].getName()
										+ " Scored: " + user[i].getHandSum());

								int dealerSum = user[0].getHandSum();

							}

						}

						ArrayList<Integer> score = new ArrayList<Integer>();

						boolean allBusted = true;
						for (int j = 0; j < user.length; j++) {
							if (user[j] != null) {
								if (!user[j].isBusted()) {
									score.add(user[j].getHandSum());
									allBusted = false;
								}
							}

						}

						Collections.sort(score);
						int highestScore = 0;
						if (score.size() < 1) {
							allBusted = true;
						} else {
							highestScore = score.get(score.size() - 1);
						}
						// System.out.println();
						for (int j = 0; j < user.length; j++) {
							if (user[j] != null) {
								if (user[j].getHandSum() == highestScore) {
									gameOut.writeUTF("[GAME RESULT] "
											+ user[j].getName() + " WIN!\n");
									informAll("[GAME RESULT] "
											+ user[j].getName() + " WIN!\n");
								}
							}
						}
						if (allBusted) {
							informAll("\n" + "DRAW \n");
							gameOut.writeUTF("[GAME RESULT] DRAW \n");
						}

						gameOut.writeUTF("-----------Game End----------\n");
						gameStart = false;
						// System.exit(0);
						// }// end if not filled
						// }// for loop all users
					}
				}// end game start

			} catch (EOFException e) {
				System.out.println("RESET GAME");
				gameFinished = true;

				eof = true;

			}

		}// end while true
	}

	public static void informAll(String message) throws IOException {
		for (int i = 0; i < user.length; i++) {
			if (user[i] != null) {
				user[i].out.writeUTF(message);
			}
		}
	}

	public static boolean isFull() throws IOException {
		boolean isFull = true;
		for (int i = 0; i < user.length; i++) {
			if (user[i] == null) {
				isFull = false;
				// System.out.println(i + " user null");
			}
		}
		return isFull;
	}
}
