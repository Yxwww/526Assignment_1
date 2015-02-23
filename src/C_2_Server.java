/**
 * Created by Yuxibro on 15-02-20.
 */
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;

public class C_2_Server {
    // Key store infomation
    final static String keyStorePath = "key/test1/server.keystore";
    final static String password = "yuxibruh"; // The Password
    final static int serverPort = 3456;
    final static boolean debugEnabled = false;   // enable debug mode

    public static void main(String[] arstring) {
        try {
            System.setProperty("javax.net.ssl.keyStore", keyStorePath);     // trust keystore
            System.setProperty("javax.net.ssl.keyStorePassword", password); // keystore password
            if (debugEnabled){
                System.setProperty("javax.net.debug", "all");
            }

            SSLServerSocketFactory server = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) server.createServerSocket(serverPort);

            SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
            InputStream sslIS = sslSocket.getInputStream();

            //For Writing Back to the Client
            OutputStream sslOS = sslSocket.getOutputStream();

            //Read from the Client
            BufferedReader bufferedreader = new BufferedReader( new InputStreamReader(sslIS));
            //System.out.println("Server listening on port" + serverPort);
            String string = null;
            while ((string = bufferedreader.readLine()) != null) {
                System.out.println("Server Received: "+ string);
                System.out.flush();
            }


            sslSocket.close();


        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}


/*DatagramSocket serverSocket;


        try {
            serverSocket = new DatagramSocket(3000);
        }
        catch (IOException e)
        {
            System.err.println("Could not create socket on port: 3000.");
            System.exit(1);
            return;
        }

        DatagramPacket myPacket = new DatagramPacket(new byte[1000], 1000);
        System.out.println ("Waiting for message.....");

        while (true)
        {
            serverSocket.receive(myPacket);
            System.out.println("Received the message: " + new String(myPacket.getData()));
            serverSocket.send(myPacket);
        }
        */