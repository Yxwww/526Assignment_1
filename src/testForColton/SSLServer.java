/**
 * @author Colton Young
 * @version CPSC 526 Assignment 1 Question C2 Server Side
 */
package testForColton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SSLServer {

	final static String password = "password";
	final static String keystore = "serverkeystore.jks";
	final static String truststore = "servertruststore.jks";
	final static int port = 3000;

	public SSLSocket socket;
	public OutputStream out;
	public InputStream in;

	
	/**
	 * Constructor
	 */
	public SSLServer() {
		try {
			SSLServerSocketFactory fact = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			SSLServerSocket servsock = (SSLServerSocket) fact
					.createServerSocket(port);
            System.out.println("null socket");
			// enforce authentication from client
			//servsock.setNeedClientAuth(true);
			// server will wait until a client initiates socket
			this.socket = (SSLSocket) servsock.accept();
			if(!this.socket.isConnected())
				System.out.println("null socket");
			
			System.out.println("socket");
			
			this.in = socket.getInputStream();
			this.out = socket.getOutputStream();

			System.out.println("Client has connected");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Waits for client to request a file, then sends file bytes over to client
	 * @param fname File name
	 * @throws IOException
	 */
	public void get(String fname) throws IOException {		
		String m;
		byte[] message;
		
		// after client sends the get request, it waits for a response
		File f = new File(fname.substring(fname.lastIndexOf("/") + 1));		// remove any dir path for security reasons
		// file exists
		if (f.exists() && !f.isDirectory()) {
			m = "ok";
			message = m.getBytes();
			// send the message to the client
			this.out.write(message);
			this.out.flush();
		}
		// file doesn't exist
		else {
			m = "dne";
			message = m.getBytes();
			// send the message to the client
			this.out.write(message);
			this.out.flush();
			// no file to send so return
			return;
		}

		// at this point, there is a file to send, so prepare it for client
		// read the byte contents of the file and send to client
		byte[] fbytes = new byte[(int) f.length()];
		FileInputStream istream = new FileInputStream(f);
		istream.read(fbytes);
		istream.close();
	}
	
	
	/**
	 * Waits for client to send file bytes over, then saves bytes to file on server
	 * @param fname Name of file
	 * @throws IOException
	 */
	public void set(String fname) throws IOException {
		// right after the client messages the server that a file will be sent,
		// it sends the file.  So read the file byte contents
		FileOutputStream ostream = null;
		String m;
		byte[] message;
		
		try {
			File f = new File(fname.substring(fname.lastIndexOf("/") + 1));		// remove dir path from file name if present
			ostream = new FileOutputStream(f);
			int b; // reads in bytes as ints
			while ((b = this.in.read()) != -1) {
				ostream.write(b);
			}
		// couldn't store the file, so tell the client
		} catch (IOException e) {
			m = "error";
			message = m.getBytes();
			this.out.write(message);
			this.out.flush();
		} finally {
			ostream.close();
		}
		// file was written successfully, tell the client
		m = "ok";
		message = m.getBytes();
		this.out.write(message);
		this.out.flush();
	}

	
	/**
	 * This is where all the work is done
	 * Server waits until client initializes session, then waits until client exits session
	 */
	public void run() {
		byte[] instr;
		String inst;
		String[] temp;
		try {
			while (true) {
				instr = new byte[500]; 		// messages from client should be smaller than 500 bytes
				this.in.read(instr);		// read the message
				inst = new String(instr); 	// convert the message to a string

				if (inst.equals("exit")) {
					// client has closed connection, stop running and close socket
					break;
				}

				// parse the message from the client, it should already be in
				// correct format
				temp = inst.split(" "); // split the string into words
				// check correct number of arguments entered

				// if set, prepare to receive the file from the client
				if (temp[0].equals("set")) {
					this.set(temp[1]);
				// if get, prepare to send the file to the client
				} else if (temp[0].equals("get")) {
					this.get(temp[1]);
				} else
					System.out.println("This should not have happend. Client sent invalid message");

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally { // close socket and streams
			try {
				this.out.close();
				this.in.close();
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	
	/**
	 * main method
	 * @param args Not used
	 */
	public static void main(String[] args) {
		// tell the system to use our keystore and truststore
		/*System.setProperty("javax.net.ssl.trustStore", truststore);
		System.setProperty("javax.net.ssl.trustStorePassword", password);*/
		System.setProperty("javax.net.ssl.keyStore", keystore);
		System.setProperty("javax.net.ssl.keyStorePassword", password);

		SSLServer server = new SSLServer();
		//server.run();
            /*try {
				server.out.close();
				server.in.close();
				server.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
	}
}
