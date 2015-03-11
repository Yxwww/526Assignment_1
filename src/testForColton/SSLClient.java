/**
 * @author Colton Young
 * @version CPSC 526 Assignment 1 Question C2 Client Side
 */
package testForColton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLClient {

	final static String message = "Enter <get/set file_name> or <exit>";
	final static String password = "password";
	final static String keystore = "clientkeystore.jks";
	final static String truststore = "clienttruststore.jks";
	final static int port = 3000;

	public String serverIP;
	public SSLSocket socket;
	public OutputStream out;
	public InputStream in;

	
	/**
	 * Constructor
	 * @param ip IP address of server
	 */
	public SSLClient(String ip) {
		// set the server IP address
		this.serverIP = ip;
		// create and connect SSLSocket to server
		SSLSocketFactory fact = (SSLSocketFactory) SSLSocketFactory
				.getDefault();
		try {
			this.socket = (SSLSocket) fact.createSocket(serverIP, port);
			this.out = socket.getOutputStream();
			this.in = socket.getInputStream();
		} catch (UnknownHostException e) {
			System.out.println("Unable to connect SSLSocket to server");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Takes a file name on client system, sends it in bytes to server to be
	 * saved
	 * 
	 * @param fname
	 *            Name of file on client system (and server if successful)
	 * @throws IOException
	 */
	public void set(String fname) throws IOException {
		File f = new File(fname);
		// check that the file exists
		if (!(f.exists() && !f.isDirectory())) {
			System.out.println("Could not find the file to send");
		}

		// message the server that a file will be sent
		String x = "set " + fname;
		this.out.write(x.getBytes());
		this.out.flush();

		// read the byte contents of the file
		byte[] fbytes = new byte[(int) f.length()];
		FileInputStream istream = new FileInputStream(f);
		istream.read(fbytes);
		istream.close();

		// send the file bytes to the server
		this.out.write(fbytes);
		this.out.flush();

		// get message from server, it arrives in bytes
		byte[] message = new byte[500]; // big enough to hold response
		this.in.read(message);
		// convert it to a string
		String mess = new String(message);
		if (mess.equals("ok"))
			System.out.println(fname + " was stored at server");
		else
			System.out.println(fname + " could not be stored at the server");

	}

	
	/**
	 * Requests a file from the server. Saves file on client if server can send
	 * it
	 * 
	 * @param fname
	 *            Name of file on server (and on client if successful)
	 * @throws IOException
	 */
	public void get(String fname) throws IOException {
		// send the message to the server
		String x = "get " + fname;
		this.out.write(x.getBytes());
		this.out.flush();

		// the server will send a response if the file was found or not
		byte[] message = new byte[500];
		this.in.read(message);
		// convert it to a string
		String mess = new String(message);
		// if server could not get file, return
		if (!mess.equals("ok")) {
			System.out.println(fname
					+ " could not be retrieved from the server");
			return;
		}

		// the server read the file ok, so prepare to receive it from server
		File f = new File(fname);
		FileOutputStream ostream = new FileOutputStream(f);
		int b; // reads in bytes as ints
		while ((b = this.in.read()) != -1) {
			ostream.write(b);
		}
		ostream.close();
		System.out.println(fname + " was retrieved from the server");
	}

	
	/**
	 * Where all the work happens Keeps taking user command until "exit" is
	 * entered
	 */
	public void run() {
		System.out.println(message);
		// read the user's input from standard input
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		String[] temp;
		try {
			while(true) {
				System.out.println(":");
				input = br.readLine();
				if(input.equals("exit")) {
					System.out.println("Session ended");
					// send the message to the server
					String x = "exit";
					this.out.write(x.getBytes());
					this.out.flush();
					break;
				}
				// parse the user input, and validate it
				temp = input.split(" ");	// split the string into words
				// check correct number of arguments entered
				if(temp.length != 2) {
					System.out.println(message);
					continue;
				}
				// if set, send server the file
				if(temp[0].equals("set")) {
					this.set(temp[1]);
				}
				else if(temp[0].equals("get")) {
					this.get(temp[1]);
				}
				else
					System.out.println(message);
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {		// close socket and streams
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
	 * 
	 * @param args
	 *            Server IP address
	 */
	public static void main(String[] args) {
		// tell the system to use our keystore and truststore
		System.setProperty("javax.net.ssl.trustStore", truststore);
		System.setProperty("javax.net.ssl.trustStorePassword", password);
		System.setProperty("javax.net.ssl.keyStore", keystore);
		System.setProperty("javax.net.ssl.keyStorePassword", password);

		// user must supply server IP as argument
		if (args.length != 1) {
			System.out.println("Supply the server IP as an argument");
			return;
		}

		// create SSLClient instance
		SSLClient client = new SSLClient(args[0]);
		client.run();
	}
}
