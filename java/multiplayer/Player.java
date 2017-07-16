import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Player  {
	static Socket socket;
	static Scanner sc = new Scanner(System.in);
	static String message;

	static DataInputStream in;
	static DataOutputStream out;

	public static void main(String[] args) throws Exception {
		
		try {

			// Player uses port 16142
			socket = new Socket(InetAddress.getLoopbackAddress()
					.getHostAddress(), 16142);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			System.out.println("Connection Successful!");

			 Input input = new Input(in);
			 Thread thread = new Thread(input);
			 thread.start();
			Scanner sc = new Scanner(System.in);

			while (true) {
				String sendMessage = sc.next();
				out.writeUTF(sendMessage);

			}
		} catch (ConnectException e) {
			System.out
					.println("Connection Refused due to exceeded users limit");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

//class Input implements Runnable {
//	DataInputStream in;
//
//	public Input(DataInputStream in) {
//		this.in = in;
//	}
//
//	synchronized public void run() {
//		while (true) {
//			String message;
//			try {
//				message = in.readUTF();
//				System.out.println(message);
//
//			} catch (EOFException e) {
//
//				System.out.println("Server Terminating....");
//				System.exit(0);
//			} catch (IOException e) {
//				// e.printStackTrace();
//			}
//
//		}
//	}
//
//}
