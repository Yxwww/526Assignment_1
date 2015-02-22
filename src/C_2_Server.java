/**
 * Created by Yuxibro on 15-02-20.
 */
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class C_2_Server {
    public static void main(String[] arstring) {
        try {
            SSLServerSocketFactory sslserversocketfactory =
                    (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslserversocket =
                    (SSLServerSocket) sslserversocketfactory.createServerSocket(3000);
            SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();

            InputStream inputstream = sslsocket.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

            String string = null;
            while ((string = bufferedreader.readLine()) != null) {
                System.out.println(string);
                System.out.flush();
            }
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