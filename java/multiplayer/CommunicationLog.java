import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Implementation of GamingLog recorded game's activities saved to
 * communication.txt
 * 
 * @author angeliacuaca
 *
 */
public class CommunicationLog {
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream writeLog;

	public static void main(String[] args) throws Exception {

		// Communication log using port 26142
		socket = new Socket(InetAddress.getLoopbackAddress().getHostAddress(),
				26142);

		// input output stream for socket
		in = new DataInputStream(socket.getInputStream());
		
		// read and write thre previous log first if exist
		String previousLog = readPrevious();
		File outputFile = new File("communication.txt");
		// output stream for file
		writeLog = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(outputFile)));
		writeLog.writeChars(previousLog);
		
		String remote = socket.getRemoteSocketAddress().toString();
		
		while (true) {
			String message;
			try {
				message = "[COMMUNICATION LOG] " + remote + " " + logTime() + in.readUTF();
				// print to communication log screen
				System.out.println(message);

				// write to communication.txt
				writeLog.writeChars(message + "\n");
			} catch (EOFException e) {
				message = "[COMMUNICATION LOG] " + remote + " " + logTime()
						+ "Server Terminated";
				System.out.println(message);
				System.out.println();

				writeLog.writeChars(message + "\n");
				writeLog.writeChars("\n");

				// flush and close after write file
				writeLog.flush();
				writeLog.close();

				System.exit(0);
			}
			catch (IOException e) {
				message = "[COMMUNICATION LOG] " + remote + " " + logTime()
						+ "Server Terminated";
				System.out.println(message);
				System.out.println();

				writeLog.writeChars(message + "\n");
				writeLog.writeChars("\n");

				// flush and close after write file
				writeLog.flush();
				writeLog.close();

				System.exit(0);
			}
		}
	}

	/**
	 * Method called to time log
	 * 
	 * @return print current date and time
	 */
	private static String logTime() {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String logTime = df.format(new Date()) + ": ";
		return logTime;
	}

	/**
	 * Read previous log file and return it as String
	 * 
	 * @return previous log read from file
	 */
	private static String readPrevious() {
		Scanner fileScanner;
		String prev = "";
		try {
			fileScanner = new Scanner(new FileReader("communication.txt"));
			while (fileScanner.hasNextLine()) {
				// System.out.println(fileScanner.nextLine());
				prev = prev + fileScanner.nextLine() + "\n";
			}
			fileScanner.close();
		} catch (FileNotFoundException e) {
			System.out
					.println("communication.txt not found... \n Creating new one...");
			return "";
		}
		return prev;
	}
}
