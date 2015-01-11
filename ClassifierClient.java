/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:	filter.java
 *	author:		Daniel Bergmann
 *	email:		db0763@bristol.ac.uk
 **************************************************************************************************/
import java.io.*;
import java.net.*;

public class ClassifierClient {
	private static final int PORT = 61337;
	private static final String SERVER = "localhost";
	private static final int MAX_START_ATTEMPTS = 5;

	private int start_attempts = 0;
	private Runtime runtime;

	private Socket requestSocket;
	private ObjectOutputStream out;
 	private ObjectInputStream in;
 	private String message;


	ClassifierClient() {
		runtime = Runtime.getRuntime();
	}

	String classify(String file) {
		try {
			//1. creating a socket to connect to the server
			requestSocket = new Socket(SERVER, PORT);
			//2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			//3: Communicating with the server
			do {
				try {
					message = (String)in.readObject();
					send("classify: "+file);

					message = (String)in.readObject();
					if (message.startsWith("class: ")) {
						send("close");
						return message.substring(7);
					}
					send("close");

				} catch(ClassNotFoundException cnfe){
					System.err.println("data received in unknown format");
				}
			} while(!message.equals("close"));
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (ConnectException ce) {
			// if the server is most likely not started
			if (start_server());
				return classify(file);

		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			//4: Closing connection
			try {
				if (in != null)
					in.close();

				if (out != null)
					out.close();

				if (requestSocket != null)
					requestSocket.close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return "";
	}

	boolean start_server() {
		if (start_attempts < MAX_START_ATTEMPTS) {
			try {
				Process server = runtime.exec("./launch-server");
				Thread.sleep(3000);
			} catch (SecurityException se) {
				// System.out.println("oops, we can't make processes. :(");
			} catch (InterruptedException ie) {
				// System.out.println("oh what..");
			} catch (IOException ioe) {
				// System.out.println(ioe);
			}
		}
		return false;
	}

	void send(String message) {
		if (out != null) {
			try {
				out.writeObject(message);
				out.flush();
			} catch(IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
}
