import java.io.*;
import java.net.*;
import java.util.Scanner;
/**
 * Implementation of Client type Player
 * @author angeliacuaca
 *
 */
public class Player  {
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;

	public static void main(String[] args) {
		System.out.println("Connecting...");

		boolean eof = false;
		boolean gameStart = false;

		try {
			socket = new Socket(InetAddress.getLoopbackAddress()
					.getHostAddress(), 16142);

			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			System.out.println("Connection Successful!");
			System.out.println("You're connected as PLAYER");
			String msgin = "", msgout = "";

			while (!eof) {

				if ((msgin = in.readUTF()).equals("START THE GAME")) {
					System.out.println(msgin);
					gameStart = true;

					// input stream thread
					Input inputThread = new Input(in);
					Thread thread = new Thread(inputThread);
					thread.start();

				}

				// if the game is running, keep receiving user input
				Scanner sc = new Scanner(System.in);
				String sendMessage;

				while (gameStart) {
					sendMessage = sc.next();
					out.writeUTF(sendMessage); // H or S expected

				}// end of gameStart

			}// end of eof

		} catch (ConnectException e) {
			System.out
					.println("Connection Refused due to exceeded users limit");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Input implements Runnable {
	DataInputStream in;

	public Input(DataInputStream in) {
		this.in = in;
	}

	public void run() {
		while (true) {
			String message;
			try {
				message = in.readUTF();
				System.out.println(message);

			} catch (EOFException e) {

				System.out.println("Server Terminating....");
				System.exit(0);
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
	}
}
