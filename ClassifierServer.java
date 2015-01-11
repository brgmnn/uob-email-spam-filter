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

public class ClassifierServer {
	private static final int PORT = 61337;
	private static final int QUEUE_SIZE = 10;
	private static final int IDLE_TIMEOUT = 30000; // in ms

	private boolean shutdown_server = false;

	private ServerSocket sock;
	private Socket connection;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String message;

	private ComponentBayesianClassifier bayes = new ComponentBayesianClassifier();

	public ClassifierServer() {
		try {
			sock = new ServerSocket(PORT, QUEUE_SIZE);
		} catch (BindException be) {
			System.out.println("port already bound...");
			shutdown_server = true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			// close connection
			try {
				if (sock != null)
					sock.close();

			} catch(IOException ioe) {
				// System.out.println("ioexception.");
			}
		}

		bayes.load_data("classifier-data.ser");
		bayes.set_component_weighting("subject", 1.5);
		bayes.set_component_weighting("body", 2.5);
	}

	public void serve_connection() {
		try {
			sock = new ServerSocket(PORT, QUEUE_SIZE);
			sock.setSoTimeout(IDLE_TIMEOUT);

			connection = sock.accept();

			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());

			send("connection established");
			try {

				do {
				message = (String)in.readObject();

				if (message.startsWith("classify: ")) {
					String cls = bayes.classify(new File(message.substring(10)));
					send("class: "+cls);
				} else if (message.equals("close")) {
					System.out.println("Closing stream.");
					send("close");
				}

				} while (!message.equals("close"));

			} catch (ClassNotFoundException cnfe) {
				System.err.println("Data received in unknown format");
			} catch (EOFException eofe) {
				System.err.println("End of file exception.");
			}

		} catch (SocketTimeoutException ste) {
			shutdown_server = true;
		} catch (BindException be) {
			System.out.println("port already bound...");
			shutdown_server = true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			// close connection
			try {
				if (in != null)
					in.close();

				if (out != null)
					out.close();

				if (sock != null)
					sock.close();

			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	void send(String message) {
		try {
			out.writeObject(message);
			out.flush();
		} catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	void run() {
		while (!shutdown_server)
			serve_connection();
	}

	public static void main(String[] args) {
		ClassifierServer svr = new ClassifierServer();
		svr.run();
	}
}
